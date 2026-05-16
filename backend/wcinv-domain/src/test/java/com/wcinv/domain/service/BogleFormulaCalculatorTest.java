package com.wcinv.domain.service;

import com.wcinv.domain.model.BogleScenarioResult;
import com.wcinv.domain.model.BogleSnapshot;
import com.wcinv.domain.model.BogleValuationLevel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BogleFormulaCalculatorTest {

    private final BogleFormulaCalculator calculator = new BogleFormulaCalculator();

    @Test
    void evaluate_shouldMatchCourseExample() {
        BogleSnapshot snapshot = new BogleSnapshot(
                "000932", "中证消费", LocalDate.of(2026, 5, 15),
                bd("32.42"), bd("0.0140"), bd("21.21"), bd("27.82"), bd("33.44"));

        List<BogleScenarioResult> result = calculator.evaluate(snapshot, bd("0.075"), 5);

        assertEquals(3, result.size());
        assertEquals("CONSERVATIVE", result.get(0).getScenario());
        assertEquals(0.76, result.get(0).getExpectedAnnualReturn().doubleValue(), 0.05);
        assertEquals("DEFENSIVE_SELL_OR_AVOID", result.get(0).getAction());
        assertEquals(BogleValuationLevel.OVERVALUED, result.get(0).getValuationLevel());

        assertEquals(5.89, result.get(1).getExpectedAnnualReturn().doubleValue(), 0.05);
        assertEquals(9.52, result.get(2).getExpectedAnnualReturn().doubleValue(), 0.05);
    }

    @Test
    void evaluate_shouldRejectInvestmentYearsLessThanThree() {
        BogleSnapshot snapshot = new BogleSnapshot(
                "000932", "中证消费", LocalDate.of(2026, 5, 15),
                bd("32.42"), bd("0.0140"), bd("21.21"), bd("27.82"), bd("33.44"));

        assertThrows(IllegalArgumentException.class, () -> calculator.evaluate(snapshot, bd("0.075"), 2));
    }

    @Test
    void evaluate_shouldTreatNegativeConservativeReturnAsSeverelyOvervalued() {
        BogleSnapshot snapshot = new BogleSnapshot(
                "000933", "中证医药", LocalDate.of(2026, 5, 15),
                bd("45.00"), bd("0.0100"), bd("20.00"), bd("30.00"), bd("40.00"));

        List<BogleScenarioResult> result = calculator.evaluate(snapshot, bd("0.075"), 5);

        assertEquals(BogleValuationLevel.SEVERE_OVERVALUED, result.get(0).getValuationLevel());
        assertEquals("DEFENSIVE_SELL_OR_AVOID", result.get(0).getAction());
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
