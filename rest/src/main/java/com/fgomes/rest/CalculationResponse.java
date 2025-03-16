package com.fgomes.rest;

import java.math.BigDecimal;

public class CalculationResponse {

    private BigDecimal result;
    private String correlationId;

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
