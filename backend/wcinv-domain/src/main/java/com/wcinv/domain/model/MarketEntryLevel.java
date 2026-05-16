package com.wcinv.domain.model;

/**
 * 市场入场等级。
 */
public enum MarketEntryLevel {

    LOW("低估入场", true),
    NEUTRAL("等待", false),
    HIGH_RISK("高估风险", false);

    private final String description;
    private final boolean allowNextStockStrategy;

    MarketEntryLevel(String description, boolean allowNextStockStrategy) {
        this.description = description;
        this.allowNextStockStrategy = allowNextStockStrategy;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAllowNextStockStrategy() {
        return allowNextStockStrategy;
    }
}