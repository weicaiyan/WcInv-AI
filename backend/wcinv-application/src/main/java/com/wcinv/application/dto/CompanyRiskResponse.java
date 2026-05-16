package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wcinv.domain.model.CompanyRiskDecision;

import java.util.List;

public class CompanyRiskResponse {
    private final String code;
    private final String name;
    @JsonProperty("risk_level")
    private final String riskLevel;
    @JsonProperty("risk_tags")
    private final List<String> riskTags;
    @JsonProperty("reject_reason")
    private final String rejectReason;
    @JsonProperty("manual_review_required")
    private final boolean manualReviewRequired;
    @JsonProperty("lazy_conclusion")
    private final String lazyConclusion;

    private CompanyRiskResponse(CompanyRiskDecision decision) {
        this.code = decision.getCode();
        this.name = decision.getName();
        this.riskLevel = decision.getRiskLevel().name();
        this.riskTags = decision.getRiskTags();
        this.rejectReason = decision.getRejectReason();
        this.manualReviewRequired = decision.isManualReviewRequired();
        this.lazyConclusion = decision.getLazyConclusion();
    }

    public static CompanyRiskResponse from(CompanyRiskDecision decision) {
        return new CompanyRiskResponse(decision);
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getRiskLevel() { return riskLevel; }
    public List<String> getRiskTags() { return riskTags; }
    public String getRejectReason() { return rejectReason; }
    public boolean isManualReviewRequired() { return manualReviewRequired; }
    public String getLazyConclusion() { return lazyConclusion; }
}
