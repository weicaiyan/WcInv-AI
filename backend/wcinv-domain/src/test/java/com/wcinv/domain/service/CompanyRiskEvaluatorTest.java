package com.wcinv.domain.service;

import com.wcinv.domain.model.CompanyRiskDecision;
import com.wcinv.domain.model.CompanyRiskLevel;
import com.wcinv.domain.model.CompanyRiskSnapshot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompanyRiskEvaluatorTest {
    private final CompanyRiskEvaluator evaluator = new CompanyRiskEvaluator();

    @Test
    void shouldRejectStCompany() {
        CompanyRiskDecision decision = evaluator.evaluate(new CompanyRiskSnapshot(
                "000001", "ST示例", true, false, false,
                false, false, false, false, true
        ));

        assertEquals(CompanyRiskLevel.REJECT, decision.getRiskLevel());
        assertTrue(decision.isManualReviewRequired());
    }

    @Test
    void shouldRejectRecentUnresolvedCsrcInvestigation() {
        CompanyRiskDecision decision = evaluator.evaluate(new CompanyRiskSnapshot(
                "000002", "风险示例", false, false, false,
                false, true, true, false, true
        ));

        assertEquals(CompanyRiskLevel.REJECT, decision.getRiskLevel());
    }

    @Test
    void shouldPassWhenNoNegativeSignalsAndUserUnderstandsBusiness() {
        CompanyRiskDecision decision = evaluator.evaluate(new CompanyRiskSnapshot(
                "000003", "正常示例", false, false, false,
                false, false, false, false, true
        ));

        assertEquals(CompanyRiskLevel.LOW, decision.getRiskLevel());
    }
}
