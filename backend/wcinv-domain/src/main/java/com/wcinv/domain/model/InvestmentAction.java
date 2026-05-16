package com.wcinv.domain.model;

/**
 * 指数温度定投动作。
 */
public enum InvestmentAction {

    HEAVY_BUY("重仓买入"),
    NORMAL_BUY("正常买入"),
    REDUCED_BUY("减量买入"),
    LIGHT_BUY("轻仓买入"),
    HOLD("持有不动"),
    SELL_HALF("分批卖出"),
    CLEAR("清仓");

    private final String description;

    InvestmentAction(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
