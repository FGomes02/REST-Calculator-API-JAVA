package com.fgomes.calculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CalculatorKafkaListener {

    @Autowired
    private CalculatorService calculatorService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "calc-requests", groupId = "calculator-group")
    public void listen(String message) throws Exception {
        CalculationRequest request = objectMapper.readValue(message, CalculationRequest.class);
        CalculationResponse response = new CalculationResponse();

        response.setCorrelationId(request.getCorrelationId());
        response.setResult(calculatorService.calculate(request.getOperation(), request.getA(), request.getB()));

        String responseMessage = objectMapper.writeValueAsString(response);
        kafkaTemplate.send("calc-responses", request.getCorrelationId(), responseMessage);
    }



}
