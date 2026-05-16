package com.wcinv.domain.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class GridPlan {
    private final BigDecimal firstBuyPrice;
    private final BigDecimal minPrice;
    private final BigDecimal gridRatio;
    private final BigDecimal maxDrawdown;
    private final BigDecimal totalGridAmount;
    private final BigDecimal perGridAmount;
    private final BigDecimal actualInvestAmount;
    private final BigDecimal expectedProfitPerCycle;
    private final BigDecimal worstCaseLoss;
    private final BigDecimal worstCaseLossRate;
    private final String lazyConclusion;
    private final List<GridLevel> levels;

    public GridPlan(BigDecimal firstBuyPrice,
                    BigDecimal minPrice,
                    BigDecimal gridRatio,
                    BigDecimal maxDrawdown,
                    BigDecimal totalGridAmount,
                    BigDecimal perGridAmount,
                    BigDecimal actualInvestAmount,
                    BigDecimal expectedProfitPerCycle,
                    BigDecimal worstCaseLoss,
                    BigDecimal worstCaseLossRate,
                    String lazyConclusion,
                    List<GridLevel> levels) {
        this.firstBuyPrice = firstBuyPrice;
        this.minPrice = minPrice;
        this.gridRatio = gridRatio;
        this.maxDrawdown = maxDrawdown;
        this.totalGridAmount = totalGridAmount;
        this.perGridAmount = perGridAmount;
        this.actualInvestAmount = actualInvestAmount;
        this.expectedProfitPerCycle = expectedProfitPerCycle;
        this.worstCaseLoss = worstCaseLoss;
        this.worstCaseLossRate = worstCaseLossRate;
        this.lazyConclusion = lazyConclusion;
        this.levels = Collections.unmodifiableList(levels);
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
    public List<GridLevel> getLevels() { return levels; }

    public static class GridLevel {
        private final int level;
        private final BigDecimal buyPrice;
        private final BigDecimal sellPrice;
        private final BigDecimal budgetAmount;
        private final int shares;
        private final BigDecimal actualAmount;
        private final BigDecimal expectedProfit;
        private final BigDecimal worstCaseLoss;

        public GridLevel(int level,
                         BigDecimal buyPrice,
                         BigDecimal sellPrice,
                         BigDecimal budgetAmount,
                         int shares,
                         BigDecimal actualAmount,
                         BigDecimal expectedProfit,
                         BigDecimal worstCaseLoss) {
            this.level = level;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.budgetAmount = budgetAmount;
            this.shares = shares;
            this.actualAmount = actualAmount;
            this.expectedProfit = expectedProfit;
            this.worstCaseLoss = worstCaseLoss;
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
