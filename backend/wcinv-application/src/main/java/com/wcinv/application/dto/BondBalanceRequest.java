package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BondBalanceRequest {
    @JsonProperty("temperature")
    private BigDecimal temperature;

    public BigDecimal getTemperature() { return temperature; }
    public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
}
