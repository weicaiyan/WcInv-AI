package com.wcinv.application.usecase;

import com.wcinv.application.dto.GridPlanRequest;
import com.wcinv.application.dto.GridPlanResponse;
import com.wcinv.domain.model.GridPlan;
import com.wcinv.domain.service.GridStrategyCalculator;

import java.math.BigDecimal;

public class CalculateGridPlanUseCase {
    private static final BigDecimal DEFAULT_GRID_RATIO = new BigDecimal("0.07");
    private static final BigDecimal DEFAULT_MAX_DRAWDOWN = new BigDecimal("0.40");
    private static final int DEFAULT_LOT_SIZE = 100;

    private final GridStrategyCalculator calculator;

    public CalculateGridPlanUseCase(GridStrategyCalculator calculator) {
        this.calculator = calculator;
    }

    public GridPlanResponse execute(GridPlanRequest request) {
        BigDecimal gridRatio = valueOrDefault(request.getGridRatio(), DEFAULT_GRID_RATIO);
        BigDecimal maxDrawdown = valueOrDefault(request.getMaxDrawdown(), DEFAULT_MAX_DRAWDOWN);
        int lotSize = request.getLotSize() == null ? DEFAULT_LOT_SIZE : request.getLotSize();

        GridPlan plan = calculator.calculate(
                request.getFirstBuyPrice(),
                request.getTotalGridAmount(),
                gridRatio,
                maxDrawdown,
                lotSize
        );
        return GridPlanResponse.from(plan);
    }

    private BigDecimal valueOrDefault(BigDecimal value, BigDecimal defaultValue) {
        return value == null ? defaultValue : value;
    }
}
