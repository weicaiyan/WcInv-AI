package com.wcinv.domain.service;

import com.wcinv.domain.model.BogleScenarioResult;
import com.wcinv.domain.model.BogleSnapshot;
import com.wcinv.domain.model.BogleValuationLevel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 博格公式计算器。
 * <p>
 * 预期年化收益率 = 股息率 + 盈利增长率 + 市盈率变化率。
 */
public class BogleFormulaCalculator {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public List<BogleScenarioResult> evaluate(BogleSnapshot snapshot, BigDecimal growthRate, int investmentYears) {
        validate(snapshot, growthRate, investmentYears);
        return List.of(
                calculate("CONSERVATIVE", "保守情况", snapshot.getPeQuantile20(),
                        snapshot, growthRate, investmentYears, new BigDecimal("8"), new BigDecimal("2")),
                calculate("AGGRESSIVE", "中等情况", snapshot.getPeQuantile50(),
                        snapshot, growthRate, investmentYears, new BigDecimal("10"), new BigDecimal("4")),
                calculate("OPTIMISTIC", "乐观情况", snapshot.getPeQuantile80(),
                        snapshot, growthRate, investmentYears, new BigDecimal("6"), null)
        );
    }

    private BogleScenarioResult calculate(String scenario, String scenarioName, BigDecimal targetPe,
                                          BogleSnapshot snapshot, BigDecimal growthRate, int investmentYears,
                                          BigDecimal buyThreshold, BigDecimal avoidThreshold) {
        double peChangeRatio = Math.pow(
                targetPe.divide(snapshot.getCurrentPe(), 10, RoundingMode.HALF_UP).doubleValue(),
                1.0d / investmentYears) - 1.0d;

        BigDecimal peChangeRate = BigDecimal.valueOf(peChangeRatio)
                .multiply(ONE_HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedReturn = snapshot.getDividendYield()
                .add(growthRate)
                .add(BigDecimal.valueOf(peChangeRatio))
                .multiply(ONE_HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);

        BogleValuationLevel level = valuationLevel(expectedReturn, buyThreshold, avoidThreshold);
        String action = action(expectedReturn, buyThreshold, avoidThreshold);
        String actionDesc = actionDesc(action);
        return new BogleScenarioResult(scenario, scenarioName, targetPe, peChangeRate,
                expectedReturn, level, action, actionDesc);
    }

    private BogleValuationLevel valuationLevel(BigDecimal expectedReturn, BigDecimal buyThreshold,
                                               BigDecimal avoidThreshold) {
        if (expectedReturn.compareTo(BigDecimal.ZERO) < 0) {
            return BogleValuationLevel.SEVERE_OVERVALUED;
        }
        if (avoidThreshold != null && expectedReturn.compareTo(avoidThreshold) < 0) {
            return BogleValuationLevel.OVERVALUED;
        }
        if (expectedReturn.compareTo(buyThreshold) > 0) {
            return BogleValuationLevel.UNDERVALUED;
        }
        return BogleValuationLevel.FAIR;
    }

    private String action(BigDecimal expectedReturn, BigDecimal buyThreshold, BigDecimal avoidThreshold) {
        if (expectedReturn.compareTo(buyThreshold) > 0) {
            return "BUY";
        }
        if (avoidThreshold != null && expectedReturn.compareTo(avoidThreshold) < 0) {
            return "DEFENSIVE_SELL_OR_AVOID";
        }
        if (expectedReturn.compareTo(BigDecimal.ZERO) < 0) {
            return "DEFENSIVE_SELL_OR_AVOID";
        }
        return "WATCH";
    }

    private String actionDesc(String action) {
        if ("BUY".equals(action)) {
            return "达到买入收益要求，可以考虑";
        }
        if ("DEFENSIVE_SELL_OR_AVOID".equals(action)) {
            return "收益太低或为负，不适合买入";
        }
        return "收益一般，继续观察";
    }

    private void validate(BogleSnapshot snapshot, BigDecimal growthRate, int investmentYears) {
        if (investmentYears < 3) {
            throw new IllegalArgumentException("博格公式投资年限必须大于等于3年");
        }
        if (snapshot == null || growthRate == null
                || snapshot.getCurrentPe() == null || snapshot.getCurrentPe().compareTo(BigDecimal.ZERO) <= 0
                || snapshot.getDividendYield() == null
                || snapshot.getPeQuantile20() == null
                || snapshot.getPeQuantile50() == null
                || snapshot.getPeQuantile80() == null) {
            throw new IllegalArgumentException("博格公式输入数据不完整");
        }
    }
}
