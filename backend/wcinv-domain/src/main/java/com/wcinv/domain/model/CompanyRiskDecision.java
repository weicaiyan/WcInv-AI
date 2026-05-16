package com.wcinv.domain.model;

import java.util.Collections;
import java.util.List;

public class CompanyRiskDecision {
    private final String code;
    private final String name;
    private final CompanyRiskLevel riskLevel;
    private final List<String> riskTags;
    private final String rejectReason;
    private final boolean manualReviewRequired;
    private final String lazyConclusion;

    public CompanyRiskDecision(String code,
                               String name,
                               CompanyRiskLevel riskLevel,
                               List<String> riskTags,
                               String rejectReason,
                               boolean manualReviewRequired,
                               String lazyConclusion) {
        this.code = code;
        this.name = name;
        this.riskLevel = riskLevel;
        this.riskTags = Collections.unmodifiableList(riskTags);
        this.rejectReason = rejectReason;
        this.manualReviewRequired = manualReviewRequired;
        this.lazyConclusion = lazyConclusion;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public CompanyRiskLevel getRiskLevel() { return riskLevel; }
    public List<String> getRiskTags() { return riskTags; }
    public String getRejectReason() { return rejectReason; }
    public boolean isManualReviewRequired() { return manualReviewRequired; }
    public String getLazyConclusion() { return lazyConclusion; }
}
