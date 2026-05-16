package com.wcinv.domain.service;

import com.wcinv.domain.model.IndexValuation;
import com.wcinv.domain.model.InvestmentAction;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 指数温度计算器 — 领域服务。
 * 温度 = (pe分位点 + pb分位点) / 2
 */
public class TemperatureCalculator {

    public double calculate(IndexValuation valuation) {
        BigDecimal pePct = valuation.getPePercentile();
        BigDecimal pbPct = valuation.getPbPercentile();
        return pePct.add(pbPct)
                .divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public InvestmentAction getAction(double temperature) {
        if (temperature < 0) {
            throw new IllegalArgumentException("温度不能为负数: " + temperature);
        }
        if (temperature < 10) {
            return InvestmentAction.HEAVY_BUY;
        } else if (temperature < 20) {
            return InvestmentAction.NORMAL_BUY;
        } else if (temperature < 25) {
            return InvestmentAction.REDUCED_BUY;
        } else if (temperature < 30) {
            return InvestmentAction.LIGHT_BUY;
        } else if (temperature < 40) {
            return InvestmentAction.HOLD;
        } else if (temperature < 50) {
            return InvestmentAction.SELL_HALF;
        } else {
            return InvestmentAction.CLEAR;
        }
    }

    public double getRatio(double temperature) {
        if (temperature < 0) {
            throw new IllegalArgumentException("温度不能为负数: " + temperature);
        }
        if (temperature < 10) {
            return 1.0;
        } else if (temperature < 20) {
            return 0.8;
        } else if (temperature < 25) {
            return 0.6;
        } else if (temperature < 30) {
            return 0.5;
        } else if (temperature < 40) {
            return 0.0;
        } else if (temperature < 50) {
            return -0.5;
        } else {
            return -1.0;
        }
    }
}
