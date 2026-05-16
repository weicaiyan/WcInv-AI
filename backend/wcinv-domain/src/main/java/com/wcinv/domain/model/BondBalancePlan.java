package com.wcinv.domain.model;

import java.math.BigDecimal;

public class BondBalancePlan {
    private final BigDecimal temperature;
    private final BigDecimal stockRatio;
    private final BigDecimal bondRatio;
    private final String lazyConclusion;

    public BondBalancePlan(BigDecimal temperature,
                           BigDecimal stockRatio,
                           BigDecimal bondRatio,
                           String lazyConclusion) {
        this.temperature = temperature;
        this.stockRatio = stockRatio;
        this.bondRatio = bondRatio;
        this.lazyConclusion = lazyConclusion;
    }

    public BigDecimal getTemperature() { return temperature; }
    public BigDecimal getStockRatio() { return stockRatio; }
    public BigDecimal getBondRatio() { return bondRatio; }
    public String getLazyConclusion() { return lazyConclusion; }
}
