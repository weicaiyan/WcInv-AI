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
    @ValueSource(doubles = {0.0, 5.0, 9.99})
    void getAction_0to10_shouldReturnHeavyBuy(double temperature) {
        assertEquals(InvestmentAction.HEAVY_BUY, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {10.0, 15.0, 19.99})
    void getAction_10to20_shouldReturnNormalBuy(double temperature) {
        assertEquals(InvestmentAction.NORMAL_BUY, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {20.0, 22.5, 24.99})
    void getAction_20to25_shouldReturnReducedBuy(double temperature) {
        assertEquals(InvestmentAction.REDUCED_BUY, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {25.0, 27.5, 29.99})
    void getAction_25to30_shouldReturnLightBuy(double temperature) {
        assertEquals(InvestmentAction.LIGHT_BUY, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {30.0, 35.0, 39.99})
    void getAction_30to40_shouldReturnHold(double temperature) {
        assertEquals(InvestmentAction.HOLD, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {40.0, 45.0, 49.99})
    void getAction_40to50_shouldReturnSellHalf(double temperature) {
        assertEquals(InvestmentAction.SELL_HALF, calculator.getAction(temperature));
    }

    @ParameterizedTest
    @ValueSource(doubles = {50.0, 60.0, 100.0})
    void getAction_50plus_shouldReturnClear(double temperature) {
        assertEquals(InvestmentAction.CLEAR, calculator.getAction(temperature));
    }

    // ── 边界值测试 ──

    @Test
    void boundary_0_shouldBeHeavyBuy() {
        assertEquals(InvestmentAction.HEAVY_BUY, calculator.getAction(0.0));
    }

    @Test
    void boundary_10_shouldBeNormalBuy() {
        assertEquals(InvestmentAction.NORMAL_BUY, calculator.getAction(10.0));
    }

    @Test
    void boundary_20_shouldBeReducedBuy() {
        assertEquals(InvestmentAction.REDUCED_BUY, calculator.getAction(20.0));
    }

    @Test
    void boundary_25_shouldBeLightBuy() {
        assertEquals(InvestmentAction.LIGHT_BUY, calculator.getAction(25.0));
    }

    @Test
    void boundary_30_shouldBeHold() {
        assertEquals(InvestmentAction.HOLD, calculator.getAction(30.0));
    }

    @Test
    void boundary_40_shouldBeSellHalf() {
        assertEquals(InvestmentAction.SELL_HALF, calculator.getAction(40.0));
    }

    @Test
    void boundary_50_shouldBeClear() {
        assertEquals(InvestmentAction.CLEAR, calculator.getAction(50.0));
    }

    // ── getRatio ──

    @ParameterizedTest
    @CsvSource({
            "0.0,  1.0",
            "5.0,  1.0",
            "9.99, 1.0",
            "10.0, 0.8",
            "15.0, 0.8",
            "19.99,0.8",
            "20.0, 0.6",
            "22.0, 0.6",
            "24.99,0.6",
            "25.0, 0.5",
            "27.0, 0.5",
            "29.99,0.5",
            "30.0, 0.0",
            "35.0, 0.0",
            "39.99,0.0",
            "40.0,-0.5",
            "45.0,-0.5",
            "49.99,-0.5",
            "50.0,-1.0",
            "70.0,-1.0",
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
    @ValueSource(doubles = {0.0, 5.0, 15.0, 22.0, 27.0, 35.0, 45.0, 60.0})
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
