package com.wcinv.domain.service;

import com.wcinv.domain.model.IndexValuation;
import com.wcinv.domain.model.InvestmentAction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureCalculatorTest {

    private final TemperatureCalculator calculator = new TemperatureCalculator();

    // ── calculate ──

    @Test
    void calculate_shouldReturnAverageOfPeAndPbPercentile() {
        IndexValuation v = new IndexValuation();
        v.setPePercentile(new BigDecimal("30.00"));
        v.setPbPercentile(new BigDecimal("20.00"));

        double result = calculator.calculate(v);
        assertEquals(25.0, result, 0.01);
    }

    @Test
    void calculate_shouldRoundToTwoDecimals() {
        IndexValuation v = new IndexValuation();
        v.setPePercentile(new BigDecimal("10.55"));
        v.setPbPercentile(new BigDecimal("20.44"));

        double result = calculator.calculate(v);
        assertEquals(15.50, result, 0.01);
    }

    // ── getAction: 7 温度区间 ──

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 7.0, 14.99})
    void getAction_0to15_shouldReturnHeavyBuy(double temperature) {
        assertEquals(InvestmentAction.HEAVY_BUY, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {15.0, 22.0, 29.99})
    void getAction_15to30_shouldReturnNormalBuy(double temperature) {
        assertEquals(InvestmentAction.NORMAL_BUY, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {30.0, 37.0, 44.99})
    void getAction_30to45_shouldReturnLightBuy(double temperature) {
        assertEquals(InvestmentAction.LIGHT_BUY, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {45.0, 52.0, 59.99})
    void getAction_45to60_shouldReturnHold(double temperature) {
        assertEquals(InvestmentAction.HOLD, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {60.0, 67.0, 74.99})
    void getAction_60to75_shouldReturnSellHalf(double temperature) {
        assertEquals(InvestmentAction.SELL_HALF, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {75.0, 85.0, 100.0})
    void getAction_75plus_shouldReturnClear(double temperature) {
        assertEquals(InvestmentAction.CLEAR, calculator.getAction(temperature));
    }

    // ── 边界值测试 ──

    @Test
    void boundary_0_shouldBeHeavyBuy() {
        assertEquals(InvestmentAction.HEAVY_BUY, calculator.getAction(0.0));
    }

    @Test
    void boundary_15_shouldBeNormalBuy() {
        assertEquals(InvestmentAction.NORMAL_BUY, calculator.getAction(15.0));
    }

    @Test
    void boundary_30_shouldBeLightBuy() {
        assertEquals(InvestmentAction.LIGHT_BUY, calculator.getAction(30.0));
    }

    @Test
    void boundary_45_shouldBeHold() {
        assertEquals(InvestmentAction.HOLD, calculator.getAction(45.0));
    }

    @Test
    void boundary_60_shouldBeSellHalf() {
        assertEquals(InvestmentAction.SELL_HALF, calculator.getAction(60.0));
    }

    @Test
    void boundary_75_shouldBeClear() {
        assertEquals(InvestmentAction.CLEAR, calculator.getAction(75.0));
    }

    // ── getRatio ──

    @ParameterizedTest
    @CsvSource({
            "0.0,  1.0",
            "7.0,  1.0",
            "14.99,1.0",
            "15.0, 0.8",
            "22.0, 0.8",
            "29.99,0.8",
            "30.0, 0.5",
            "37.0, 0.5",
            "44.99,0.5",
            "45.0, 0.0",
            "52.0, 0.0",
            "59.99,0.0",
            "60.0,-0.5",
            "67.0,-0.5",
            "74.99,-0.5",
            "75.0,-1.0",
            "85.0,-1.0",
    })
    void getRatio_shouldReturnCorrectValue(double temperature, double expectedRatio) {
        assertEquals(expectedRatio, calculator.getRatio(temperature), 0.001);
    }

    // ── 异常 ──

    @Test
    void getAction_negative_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> calculator.getAction(-1.0));
    }

    @Test
    void getRatio_negative_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> calculator.getRatio(-0.1));
    }

    // ── 一致性: getAction 和 getRatio 同一区间对应一致 ──

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 7.0, 22.0, 37.0, 52.0, 67.0, 85.0})
    void getActionAndGetRatio_shouldBeConsistent(double temperature) {
        InvestmentAction action = calculator.getAction(temperature);
        double ratio = calculator.getRatio(temperature);

        switch (action) {
            case HEAVY_BUY:
                assertEquals(1.0, ratio, 0.001);
                break;
            case NORMAL_BUY:
                assertEquals(0.8, ratio, 0.001);
                break;
            case REDUCED_BUY:
                assertEquals(0.6, ratio, 0.001);
                break;
            case LIGHT_BUY:
                assertEquals(0.5, ratio, 0.001);
                break;
            case HOLD:
                assertEquals(0.0, ratio, 0.001);
                break;
            case SELL_HALF:
                assertEquals(-0.5, ratio, 0.001);
                break;
            case CLEAR:
                assertEquals(-1.0, ratio, 0.001);
                break;
            default:
                fail("未知动作: " + action);
        }
    }
}
