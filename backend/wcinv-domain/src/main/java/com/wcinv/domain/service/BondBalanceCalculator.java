package com.wcinv.domain.service;

import com.wcinv.domain.model.BondBalancePlan;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BondBalanceCalculator {
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final BigDecimal TEN = new BigDecimal("10");

    public BondBalancePlan calculate(BigDecimal temperature) {
        validate(temperature);

        // temperature = 0 is special: 0% bonds
        // otherwise bond% = ceil(temperature / 10) * 10
        BigDecimal bondRatio;
        if (temperature.compareTo(BigDecimal.ZERO) == 0) {
            bondRatio = BigDecimal.ZERO;
        } else {
            bondRatio = temperature.divide(TEN, 0, RoundingMode.CEILING)
                    .multiply(TEN);
            if (bondRatio.compareTo(HUNDRED) > 0) {
                bondRatio = HUNDRED;
            }
        }

        BigDecimal stockRatio = HUNDRED.subtract(bondRatio);
        String lazyConclusion = buildConclusion(temperature, stockRatio, bondRatio);

        return new BondBalancePlan(
                temperature.setScale(1, RoundingMode.HALF_UP),
                stockRatio,
                bondRatio,
                lazyConclusion
        );
    }

    private void validate(BigDecimal temperature) {
        if (temperature == null) {
            throw new IllegalArgumentException("中证全指温度不能为空");
        }
        if (temperature.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("温度不能为负数，当前值：" + temperature);
        }
    }

    private String buildConclusion(BigDecimal temperature,
                                   BigDecimal stockRatio,
                                   BigDecimal bondRatio) {
        int t = temperature.intValue();
        if (t < 20) {
            return "温度" + temperature.setScale(1, RoundingMode.HALF_UP)
                    + "°偏低——股票" + stockRatio + "% 债券" + bondRatio
                    + "%，可以大胆多配股票。";
        } else if (t < 40) {
            return "温度" + temperature.setScale(1, RoundingMode.HALF_UP)
                    + "°适中偏低——股票" + stockRatio + "% 债券" + bondRatio
                    + "%，股票为主、债券兜底。";
        } else if (t < 60) {
            return "温度" + temperature.setScale(1, RoundingMode.HALF_UP)
                    + "°适中——股票" + stockRatio + "% 债券" + bondRatio
                    + "%，股债各半，稳健均衡。";
        } else if (t < 80) {
            return "温度" + temperature.setScale(1, RoundingMode.HALF_UP)
                    + "°偏高——股票" + stockRatio + "% 债券" + bondRatio
                    + "%，多配债券，防御为主。";
        } else {
            return "温度" + temperature.setScale(1, RoundingMode.HALF_UP)
                    + "°很高——股票" + stockRatio + "% 债券" + bondRatio
                    + "%，股市过热，清仓观望。";
        }
    }
}
