package com.wcinv.domain.service;

import com.wcinv.domain.model.GridPlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class GridStrategyCalculator {
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    private static final int DEFAULT_LOT_SIZE = 100;

    public GridPlan calculate(BigDecimal firstBuyPrice,
                              BigDecimal totalGridAmount,
                              BigDecimal gridRatio,
                              BigDecimal maxDrawdown,
                              int lotSize) {
        validate(firstBuyPrice, totalGridAmount, gridRatio, maxDrawdown, lotSize);

        int levelCount = maxDrawdown.divide(gridRatio, 0, RoundingMode.CEILING).intValue() + 1;
        BigDecimal minPrice = money(firstBuyPrice.multiply(ONE.subtract(maxDrawdown)));
        BigDecimal perGridAmount = money(totalGridAmount.divide(new BigDecimal(levelCount), 8, RoundingMode.DOWN));

        List<GridPlan.GridLevel> levels = new ArrayList<>();
        BigDecimal buyPrice = money(firstBuyPrice);
        BigDecimal previousBuyPrice = null;
        BigDecimal actualInvestAmount = BigDecimal.ZERO;
        BigDecimal expectedProfitPerCycle = BigDecimal.ZERO;
        BigDecimal worstCaseLoss = BigDecimal.ZERO;

        for (int i = 1; i <= levelCount; i++) {
            BigDecimal sellPrice = i == 1
                    ? money(buyPrice.multiply(ONE.add(gridRatio)))
                    : previousBuyPrice;
            int shares = perGridAmount.divide(buyPrice, 0, RoundingMode.DOWN)
                    .divide(new BigDecimal(lotSize), 0, RoundingMode.DOWN)
                    .multiply(new BigDecimal(lotSize))
                    .intValue();
            BigDecimal actualAmount = money(buyPrice.multiply(new BigDecimal(shares)));
            BigDecimal expectedProfit = money(sellPrice.subtract(buyPrice).multiply(new BigDecimal(shares)));
            BigDecimal levelWorstLoss = money(max(BigDecimal.ZERO, buyPrice.subtract(minPrice)).multiply(new BigDecimal(shares)));

            levels.add(new GridPlan.GridLevel(
                    i,
                    buyPrice,
                    sellPrice,
                    perGridAmount,
                    shares,
                    actualAmount,
                    expectedProfit,
                    levelWorstLoss
            ));

            actualInvestAmount = actualInvestAmount.add(actualAmount);
            expectedProfitPerCycle = expectedProfitPerCycle.add(expectedProfit);
            worstCaseLoss = worstCaseLoss.add(levelWorstLoss);

            previousBuyPrice = buyPrice;
            buyPrice = money(buyPrice.multiply(ONE.subtract(gridRatio)));
        }

        actualInvestAmount = money(actualInvestAmount);
        expectedProfitPerCycle = money(expectedProfitPerCycle);
        worstCaseLoss = money(worstCaseLoss);
        BigDecimal worstLossRate = actualInvestAmount.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : percent(worstCaseLoss.divide(actualInvestAmount, 8, RoundingMode.HALF_UP));
        String conclusion = buildConclusion(levelCount, perGridAmount, actualInvestAmount, worstCaseLoss, worstLossRate);

        return new GridPlan(
                money(firstBuyPrice),
                minPrice,
                percent(gridRatio),
                percent(maxDrawdown),
                money(totalGridAmount),
                perGridAmount,
                actualInvestAmount,
                expectedProfitPerCycle,
                worstCaseLoss,
                worstLossRate,
                conclusion,
                levels
        );
    }

    public GridPlan calculate(BigDecimal firstBuyPrice, BigDecimal totalGridAmount) {
        return calculate(firstBuyPrice, totalGridAmount, new BigDecimal("0.07"), new BigDecimal("0.40"), DEFAULT_LOT_SIZE);
    }

    private void validate(BigDecimal firstBuyPrice,
                          BigDecimal totalGridAmount,
                          BigDecimal gridRatio,
                          BigDecimal maxDrawdown,
                          int lotSize) {
        if (firstBuyPrice == null || firstBuyPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("第一网买入价必须大于0");
        }
        if (totalGridAmount == null || totalGridAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("网格总资金必须大于0");
        }
        if (gridRatio == null || gridRatio.compareTo(BigDecimal.ZERO) <= 0 || gridRatio.compareTo(new BigDecimal("0.50")) > 0) {
            throw new IllegalArgumentException("网格比例必须在0到50%之间");
        }
        if (maxDrawdown == null || maxDrawdown.compareTo(BigDecimal.ZERO) <= 0 || maxDrawdown.compareTo(new BigDecimal("0.90")) > 0) {
            throw new IllegalArgumentException("最大下跌幅度必须在0到90%之间");
        }
        if (lotSize <= 0) {
            throw new IllegalArgumentException("交易单位必须大于0");
        }
    }

    private String buildConclusion(int levelCount,
                                   BigDecimal perGridAmount,
                                   BigDecimal actualInvestAmount,
                                   BigDecimal worstCaseLoss,
                                   BigDecimal worstLossRate) {
        if (actualInvestAmount.compareTo(BigDecimal.ZERO) == 0) {
            return "钱太少，按100股整数倍买不了，先别做网格。";
        }
        return "生成" + levelCount + "档，每档约" + perGridAmount.setScale(0, RoundingMode.HALF_UP)
                + "元；最坏亏损约" + worstCaseLoss.setScale(0, RoundingMode.HALF_UP)
                + "元（" + worstLossRate.setScale(1, RoundingMode.HALF_UP) + "%）。";
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal percent(BigDecimal value) {
        return value.multiply(HUNDRED).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal max(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) >= 0 ? left : right;
    }
}
