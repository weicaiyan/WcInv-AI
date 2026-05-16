package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wcinv.domain.model.BondBalancePlan;

import java.math.BigDecimal;

public class BondBalanceResponse {
    @JsonProperty("temperature")
    private final BigDecimal temperature;
    @JsonProperty("stock_ratio")
    private final BigDecimal stockRatio;
    @JsonProperty("bond_ratio")
    private final BigDecimal bondRatio;
    @JsonProperty("lazy_conclusion")
    private final String lazyConclusion;

    private BondBalanceResponse(BondBalancePlan plan) {
        this.temperature = plan.getTemperature();
        this.stockRatio = plan.getStockRatio();
        this.bondRatio = plan.getBondRatio();
        this.lazyConclusion = plan.getLazyConclusion();
    }

    public static BondBalanceResponse from(BondBalancePlan plan) {
        return new BondBalanceResponse(plan);
    }

    public BigDecimal getTemperature() { return temperature; }
    public BigDecimal getStockRatio() { return stockRatio; }
    public BigDecimal getBondRatio() { return bondRatio; }
    public String getLazyConclusion() { return lazyConclusion; }
}
