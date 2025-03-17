package com.fgomes.calculator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class CalculatorServiceTest {

    private final CalculatorService calculatorService = new CalculatorService();

    @Test
    void testSum() {
        BigDecimal result = calculatorService.calculate("sum", new BigDecimal("5"), new BigDecimal("3"));
        Assertions.assertEquals(new BigDecimal("8"), result);
    }

    @Test
    void testSubtraction() {
        BigDecimal result = calculatorService.calculate("subtraction", new BigDecimal("10"), new BigDecimal("4"));
        Assertions.assertEquals(new BigDecimal("6"), result);
    }

    @Test
    void testMultiplication() {
        BigDecimal result = calculatorService.calculate("multiplication", new BigDecimal("2"), new BigDecimal("3"));
        Assertions.assertEquals(new BigDecimal("6"), result);
    }

    @Test
    void testDivision() {
        BigDecimal result = calculatorService.calculate("division", new BigDecimal("10"), new BigDecimal("4"));
        Assertions.assertEquals(new BigDecimal("2.5000000000"), result);
    }

    @Test
    void testDivisionByZero() {
        Assertions.assertThrows(ArithmeticException.class, () -> {
            calculatorService.calculate("division", new BigDecimal("10"), BigDecimal.ZERO);
        });
    }

    @Test
    void testInvalidOperation() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            calculatorService.calculate("unknown_op", new BigDecimal("1"), new BigDecimal("1"));
        });
    }
}
