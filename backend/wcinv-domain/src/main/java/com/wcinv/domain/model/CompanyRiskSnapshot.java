package com.wcinv.domain.model;

public class CompanyRiskSnapshot {
    private final String code;
    private final String name;
    private final boolean st;
    private final boolean starSt;
    private final boolean nst;
    private final boolean scandalFound;
    private final boolean csrcInvestigationFound;
    private final boolean unresolvedRecentIssue;
    private final boolean businessDeteriorating;
    private final boolean userUnderstandsBusiness;

    public CompanyRiskSnapshot(String code,
                               String name,
                               boolean st,
                               boolean starSt,
                               boolean nst,
                               boolean scandalFound,
                               boolean csrcInvestigationFound,
                               boolean unresolvedRecentIssue,
                               boolean businessDeteriorating,
                               boolean userUnderstandsBusiness) {
        this.code = code;
        this.name = name;
        this.st = st;
        this.starSt = starSt;
        this.nst = nst;
        this.scandalFound = scandalFound;
        this.csrcInvestigationFound = csrcInvestigationFound;
        this.unresolvedRecentIssue = unresolvedRecentIssue;
        this.businessDeteriorating = businessDeteriorating;
        this.userUnderstandsBusiness = userUnderstandsBusiness;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public boolean isSt() { return st; }
    public boolean isStarSt() { return starSt; }
    public boolean isNst() { return nst; }
    public boolean isScandalFound() { return scandalFound; }
    public boolean isCsrcInvestigationFound() { return csrcInvestigationFound; }
    public boolean isUnresolvedRecentIssue() { return unresolvedRecentIssue; }
    public boolean isBusinessDeteriorating() { return businessDeteriorating; }
    public boolean isUserUnderstandsBusiness() { return userUnderstandsBusiness; }
}
