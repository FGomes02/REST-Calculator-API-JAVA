package com.fgomes.calculator;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorService {
    public BigDecimal calculate(String operation, BigDecimal a, BigDecimal b) {
        return switch (operation) {
            case "sum" -> a.add(b);
            case "subtraction" -> a.subtract(b);
            case "multiplication" -> a.multiply(b);
            case "division" -> {
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                yield a.divide(b, 10, RoundingMode.HALF_UP);
            }
            default -> throw new IllegalArgumentException("This operations is invalid: " + operation);
        };
    }
}

