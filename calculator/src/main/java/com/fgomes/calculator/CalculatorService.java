package com.fgomes.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {
    private static final Logger logger = LoggerFactory.getLogger(CalculatorService.class);

    public BigDecimal calculate(String operation, BigDecimal a, BigDecimal b) {
        logger.info("Calculating operation '{}' for operands {} and {}", operation, a, b);
        switch (operation) {
            case "sum":
                return a.add(b);
            case "subtraction":
                return a.subtract(b);
            case "multiplication":
                return a.multiply(b);
            case "division":
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    logger.error("Division by zero attempted with a={} and b={}", a, b);
                    throw new ArithmeticException("Division by zero");
                }
                return a.divide(b, 10, RoundingMode.HALF_UP);
            default:
                logger.error("Invalid operation: {}", operation);
                throw new IllegalArgumentException("Invalid operation: " + operation);
        }
    }
}

