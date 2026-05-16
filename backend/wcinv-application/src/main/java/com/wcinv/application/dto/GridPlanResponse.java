package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wcinv.domain.model.GridPlan;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class GridPlanResponse {
    @JsonProperty("first_buy_price")
    private final BigDecimal firstBuyPrice;
    @JsonProperty("min_price")
    private final BigDecimal minPrice;
    @JsonProperty("grid_ratio")
    private final BigDecimal gridRatio;
    @JsonProperty("max_drawdown")
    private final BigDecimal maxDrawdown;
    @JsonProperty("total_grid_amount")
    private final BigDecimal totalGridAmount;
    @JsonProperty("per_grid_amount")
    private final BigDecimal perGridAmount;
    @JsonProperty("actual_invest_amount")
    private final BigDecimal actualInvestAmount;
    @JsonProperty("expected_profit_per_cycle")
    private final BigDecimal expectedProfitPerCycle;
    @JsonProperty("worst_case_loss")
    private final BigDecimal worstCaseLoss;
    @JsonProperty("worst_case_loss_rate")
    private final BigDecimal worstCaseLossRate;
    @JsonProperty("lazy_conclusion")
    private final String lazyConclusion;
    private final List<Level> levels;

    private GridPlanResponse(GridPlan plan) {
        this.firstBuyPrice = plan.getFirstBuyPrice();
        this.minPrice = plan.getMinPrice();
        this.gridRatio = plan.getGridRatio();
        this.maxDrawdown = plan.getMaxDrawdown();
        this.totalGridAmount = plan.getTotalGridAmount();
        this.perGridAmount = plan.getPerGridAmount();
        this.actualInvestAmount = plan.getActualInvestAmount();
        this.expectedProfitPerCycle = plan.getExpectedProfitPerCycle();
        this.worstCaseLoss = plan.getWorstCaseLoss();
        this.worstCaseLossRate = plan.getWorstCaseLossRate();
        this.lazyConclusion = plan.getLazyConclusion();
        this.levels = plan.getLevels().stream().map(Level::new).collect(Collectors.toList());
    }

    public static GridPlanResponse from(GridPlan plan) {
        return new GridPlanResponse(plan);
    }

    public BigDecimal getFirstBuyPrice() { return firstBuyPrice; }
    public BigDecimal getMinPrice() { return minPrice; }
    public BigDecimal getGridRatio() { return gridRatio; }
    public BigDecimal getMaxDrawdown() { return maxDrawdown; }
    public BigDecimal getTotalGridAmount() { return totalGridAmount; }
    public BigDecimal getPerGridAmount() { return perGridAmount; }
    public BigDecimal getActualInvestAmount() { return actualInvestAmount; }
    public BigDecimal getExpectedProfitPerCycle() { return expectedProfitPerCycle; }
    public BigDecimal getWorstCaseLoss() { return worstCaseLoss; }
    public BigDecimal getWorstCaseLossRate() { return worstCaseLossRate; }
    public String getLazyConclusion() { return lazyConclusion; }
    public List<Level> getLevels() { return levels; }

    public static class Level {
        private final int level;
        @JsonProperty("buy_price")
        private final BigDecimal buyPrice;
        @JsonProperty("sell_price")
        private final BigDecimal sellPrice;
        @JsonProperty("budget_amount")
        private final BigDecimal budgetAmount;
        private final int shares;
        @JsonProperty("actual_amount")
        private final BigDecimal actualAmount;
        @JsonProperty("expected_profit")
        private final BigDecimal expectedProfit;
        @JsonProperty("worst_case_loss")
        private final BigDecimal worstCaseLoss;

        private Level(GridPlan.GridLevel level) {
            this.level = level.getLevel();
            this.buyPrice = level.getBuyPrice();
            this.sellPrice = level.getSellPrice();
            this.budgetAmount = level.getBudgetAmount();
            this.shares = level.getShares();
            this.actualAmount = level.getActualAmount();
            this.expectedProfit = level.getExpectedProfit();
            this.worstCaseLoss = level.getWorstCaseLoss();
        }

        public int getLevel() { return level; }
        public BigDecimal getBuyPrice() { return buyPrice; }
        public BigDecimal getSellPrice() { return sellPrice; }
        public BigDecimal getBudgetAmount() { return budgetAmount; }
        public int getShares() { return shares; }
        public BigDecimal getActualAmount() { return actualAmount; }
        public BigDecimal getExpectedProfit() { return expectedProfit; }
        public BigDecimal getWorstCaseLoss() { return worstCaseLoss; }
    }
}
