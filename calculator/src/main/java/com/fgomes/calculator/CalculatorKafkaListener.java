package com.fgomes.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Component
public class CalculatorKafkaListener {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorKafkaListener.class);

    @Autowired
    private CalculatorService calculatorService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "calc-requests", groupId = "calculator-group")
    public void listen(String message) throws Exception {
        logger.info("Received Kafka message: {}", message);
        CalculationRequest request = objectMapper.readValue(message, CalculationRequest.class);
        CalculationResponse response = new CalculationResponse();
        response.setCorrelationId(request.getCorrelationId());
        try {
            response.setResult(calculatorService.calculate(request.getOperation(), request.getA(), request.getB()));
        } catch (Exception e) {
            logger.error("Error during calculation", e);
            // Here you could set an error field in the response if desired
            throw e;
        }
        String responseMessage = objectMapper.writeValueAsString(response);
        logger.info("Sending Kafka response: {} for correlationId: {}", responseMessage, request.getCorrelationId());
        kafkaTemplate.send("calc-responses", request.getCorrelationId(), responseMessage);
    }
}
