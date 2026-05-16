package com.wcinv.domain.service;

import com.wcinv.domain.model.BondBalancePlan;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BondBalanceCalculatorTest {
    private final BondBalanceCalculator calculator = new BondBalanceCalculator();

    @Test
    void zeroDegrees_shouldBeAllStock() {
        BondBalancePlan plan = calculator.calculate(BigDecimal.ZERO);
        assertEquals(new BigDecimal("100"), plan.getStockRatio());
        assertEquals(BigDecimal.ZERO, plan.getBondRatio());
    }

    @Test
    void at381Degrees_shouldBe6040() {
        BondBalancePlan plan = calculator.calculate(new BigDecimal("38.1"));
        assertEquals(new BigDecimal("60"), plan.getStockRatio());
        assertEquals(new BigDecimal("40"), plan.getBondRatio());
    }

    @Test
    void at10Degrees_shouldBe8020() {
        BondBalancePlan plan = calculator.calculate(new BigDecimal("10"));
        assertEquals(new BigDecimal("90"), plan.getStockRatio());
        assertEquals(new BigDecimal("10"), plan.getBondRatio());
    }

    @Test
    void at25Degrees_shouldBe7030() {
        BondBalancePlan plan = calculator.calculate(new BigDecimal("25"));
        assertEquals(new BigDecimal("70"), plan.getStockRatio());
        assertEquals(new BigDecimal("30"), plan.getBondRatio());
    }

    @Test
    void at55Degrees_shouldBe4060() {
        BondBalancePlan plan = calculator.calculate(new BigDecimal("55"));
        assertEquals(new BigDecimal("40"), plan.getStockRatio());
        assertEquals(new BigDecimal("60"), plan.getBondRatio());
    }

    @Test
    void at90Degrees_shouldBe100Bond() {
        BondBalancePlan plan = calculator.calculate(new BigDecimal("90"));
        assertEquals(new BigDecimal("10"), plan.getStockRatio());
        assertEquals(new BigDecimal("90"), plan.getBondRatio());
    }

    @Test
    void at100Degrees_shouldBeAllBond() {
        BondBalancePlan plan = calculator.calculate(new BigDecimal("100"));
        assertEquals(BigDecimal.ZERO, plan.getStockRatio());
        assertEquals(new BigDecimal("100"), plan.getBondRatio());
    }

    @Test
    void above100_shouldBeCappedAt100Bond() {
        BondBalancePlan plan = calculator.calculate(new BigDecimal("120"));
        assertEquals(BigDecimal.ZERO, plan.getStockRatio());
        assertEquals(new BigDecimal("100"), plan.getBondRatio());
    }

    @Test
    void negativeTemperature_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate(new BigDecimal("-1")));
    }

    @Test
    void nullTemperature_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate(null));
    }
}
