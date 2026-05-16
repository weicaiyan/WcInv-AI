package com.wcinv.application.usecase;

import com.wcinv.application.dto.CompanyRiskRequest;
import com.wcinv.application.dto.CompanyRiskResponse;
import com.wcinv.domain.model.CompanyRiskDecision;
import com.wcinv.domain.model.CompanyRiskSnapshot;
import com.wcinv.domain.service.CompanyRiskEvaluator;

public class EvaluateCompanyRiskUseCase {
    private final CompanyRiskEvaluator evaluator;

    public EvaluateCompanyRiskUseCase(CompanyRiskEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    public CompanyRiskResponse execute(CompanyRiskRequest request) {
        CompanyRiskSnapshot snapshot = new CompanyRiskSnapshot(
                request.getCode(),
                request.getName(),
                request.isSt(),
                request.isStarSt(),
                request.isNst(),
                request.isScandalFound(),
                request.isCsrcInvestigationFound(),
                request.isUnresolvedRecentIssue(),
                request.isBusinessDeteriorating(),
                request.isUserUnderstandsBusiness()
        );
        CompanyRiskDecision decision = evaluator.evaluate(snapshot);
        return CompanyRiskResponse.from(decision);
    }
}
