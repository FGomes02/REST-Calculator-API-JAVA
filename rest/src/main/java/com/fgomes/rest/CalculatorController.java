package com.fgomes.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

@RestController
public class CalculatorController {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorController.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String REQUEST_TOPIC = "calc-requests";
    private final String RESPONSE_TOPIC = "calc-responses";

    @GetMapping("/{operation}")
    public ResponseEntity<?> calculate(@PathVariable String operation,
                                       @RequestParam("a") String a,
                                       @RequestParam("b") String b) throws Exception {

        String correlationId = UUID.randomUUID().toString();
        logger.info("Received REST request for operation '{}' with a={} and b={}", operation, a, b);

        CalculationRequest request = new CalculationRequest();
        request.setOperation(operation);
        request.setA(new BigDecimal(a));
        request.setB(new BigDecimal(b));
        request.setCorrelationId(correlationId);

        String requestJson = objectMapper.writeValueAsString(request);
        logger.info("Sending Kafka message to topic '{}' with correlationId {}", REQUEST_TOPIC, correlationId);
        kafkaTemplate.send(REQUEST_TOPIC, correlationId, requestJson);

        // Create Kafka consumer to wait for response
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("group.id", "rest-response-group-" + correlationId);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("auto.offset.reset", "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(RESPONSE_TOPIC));

        AtomicReference<CalculationResponse> calcResponse = new AtomicReference<>();
        long timeout = System.currentTimeMillis() + 10000; // 10 seconds timeout
        while (System.currentTimeMillis() < timeout && calcResponse.get() == null) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                if (correlationId.equals(record.key())) {
                    try {
                        CalculationResponse response = objectMapper.readValue(record.value(), CalculationResponse.class);
                        logger.info("Received Kafka response '{}' for correlationId {}", record.value(), correlationId);
                        calcResponse.set(response);
                    } catch (Exception e) {
                        logger.error("Error parsing Kafka response", e);
                    }
                }
            });
        }
        consumer.close();

        if (calcResponse.get() == null) {
            logger.warn("Timeout waiting for calculation response for correlationId: {}", correlationId);
            return ResponseEntity.status(504).body("Timeout waiting for calculation response");
        }

        // Add the correlationId to the response header
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Correlation-Id", correlationId);
        logger.info("Returning response for correlationId: {}", correlationId);
        return ResponseEntity.ok().headers(headers).body(calcResponse.get());
    }
}