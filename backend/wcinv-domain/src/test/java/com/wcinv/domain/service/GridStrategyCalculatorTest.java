package com.wcinv.domain.service;

import com.wcinv.domain.model.GridPlan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GridStrategyCalculatorTest {

    private final GridStrategyCalculator calculator = new GridStrategyCalculator();

    @Test
    void shouldGenerateCourseExampleGridLevels() {
        GridPlan plan = calculator.calculate(
                new BigDecimal("2.72"),
                new BigDecimal("70000"),
                new BigDecimal("0.07"),
                new BigDecimal("0.40"),
                100
        );

        assertEquals(new BigDecimal("1.63"), plan.getMinPrice());
        assertEquals(7, plan.getLevels().size());
        assertEquals(new BigDecimal("2.72"), plan.getLevels().get(0).getBuyPrice());
        assertEquals(new BigDecimal("2.91"), plan.getLevels().get(0).getSellPrice());
        assertEquals(new BigDecimal("2.53"), plan.getLevels().get(1).getBuyPrice());
        assertEquals(new BigDecimal("2.72"), plan.getLevels().get(1).getSellPrice());
        assertEquals(new BigDecimal("1.77"), plan.getLevels().get(6).getBuyPrice());
        assertEquals(3600, plan.getLevels().get(0).getShares());
        assertEquals(3900, plan.getLevels().get(1).getShares());
    }
}
