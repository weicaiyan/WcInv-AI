package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class GridPlanRequest {
    @JsonProperty("first_buy_price")
    private BigDecimal firstBuyPrice;

    @JsonProperty("total_grid_amount")
    private BigDecimal totalGridAmount;

    @JsonProperty("grid_ratio")
    private BigDecimal gridRatio;

    @JsonProperty("max_drawdown")
    private BigDecimal maxDrawdown;

    @JsonProperty("lot_size")
    private Integer lotSize;

    public BigDecimal getFirstBuyPrice() { return firstBuyPrice; }
    public void setFirstBuyPrice(BigDecimal firstBuyPrice) { this.firstBuyPrice = firstBuyPrice; }

    public BigDecimal getTotalGridAmount() { return totalGridAmount; }
    public void setTotalGridAmount(BigDecimal totalGridAmount) { this.totalGridAmount = totalGridAmount; }

    public BigDecimal getGridRatio() { return gridRatio; }
    public void setGridRatio(BigDecimal gridRatio) { this.gridRatio = gridRatio; }

    public BigDecimal getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(BigDecimal maxDrawdown) { this.maxDrawdown = maxDrawdown; }

    public Integer getLotSize() { return lotSize; }
    public void setLotSize(Integer lotSize) { this.lotSize = lotSize; }
}
