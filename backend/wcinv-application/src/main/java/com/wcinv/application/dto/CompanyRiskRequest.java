package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyRiskRequest {
    private String code;
    private String name;
    private boolean st;
    @JsonProperty("star_st")
    private boolean starSt;
    private boolean nst;
    @JsonProperty("scandal_found")
    private boolean scandalFound;
    @JsonProperty("csrc_investigation_found")
    private boolean csrcInvestigationFound;
    @JsonProperty("unresolved_recent_issue")
    private boolean unresolvedRecentIssue;
    @JsonProperty("business_deteriorating")
    private boolean businessDeteriorating;
    @JsonProperty("user_understands_business")
    private boolean userUnderstandsBusiness;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isSt() { return st; }
    public void setSt(boolean st) { this.st = st; }
    public boolean isStarSt() { return starSt; }
    public void setStarSt(boolean starSt) { this.starSt = starSt; }
    public boolean isNst() { return nst; }
    public void setNst(boolean nst) { this.nst = nst; }
    public boolean isScandalFound() { return scandalFound; }
    public void setScandalFound(boolean scandalFound) { this.scandalFound = scandalFound; }
    public boolean isCsrcInvestigationFound() { return csrcInvestigationFound; }
    public void setCsrcInvestigationFound(boolean csrcInvestigationFound) { this.csrcInvestigationFound = csrcInvestigationFound; }
    public boolean isUnresolvedRecentIssue() { return unresolvedRecentIssue; }
    public void setUnresolvedRecentIssue(boolean unresolvedRecentIssue) { this.unresolvedRecentIssue = unresolvedRecentIssue; }
    public boolean isBusinessDeteriorating() { return businessDeteriorating; }
    public void setBusinessDeteriorating(boolean businessDeteriorating) { this.businessDeteriorating = businessDeteriorating; }
    public boolean isUserUnderstandsBusiness() { return userUnderstandsBusiness; }
    public void setUserUnderstandsBusiness(boolean userUnderstandsBusiness) { this.userUnderstandsBusiness = userUnderstandsBusiness; }
}
