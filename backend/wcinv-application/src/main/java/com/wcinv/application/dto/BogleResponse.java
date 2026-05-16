package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class BogleResponse {

    @JsonProperty("trade_date")
    private String tradeDate;
    @JsonProperty("growth_rate")
    private double growthRate;
    @JsonProperty("investment_years")
    private int investmentYears;
    @JsonProperty("lazy_conclusion")
    private String lazyConclusion;
    private List<IndexResult> indices = new ArrayList<>();

    public String getTradeDate() { return tradeDate; }
    public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }
    public double getGrowthRate() { return growthRate; }
    public void setGrowthRate(double growthRate) { this.growthRate = growthRate; }
    public int getInvestmentYears() { return investmentYears; }
    public void setInvestmentYears(int investmentYears) { this.investmentYears = investmentYears; }
    public String getLazyConclusion() { return lazyConclusion; }
    public void setLazyConclusion(String lazyConclusion) { this.lazyConclusion = lazyConclusion; }
    public List<IndexResult> getIndices() { return indices; }
    public void setIndices(List<IndexResult> indices) { this.indices = indices; }

    public static class IndexResult {
        @JsonProperty("index_code")
        private String indexCode;
        @JsonProperty("index_name")
        private String indexName;
        @JsonProperty("trade_date")
        private String tradeDate;
        @JsonProperty("current_pe")
        private double currentPe;
        @JsonProperty("dividend_yield")
        private double dividendYield;
        private List<Scenario> scenarios = new ArrayList<>();
        private String conclusion;

        public String getIndexCode() { return indexCode; }
        public void setIndexCode(String indexCode) { this.indexCode = indexCode; }
        public String getIndexName() { return indexName; }
        public void setIndexName(String indexName) { this.indexName = indexName; }
        public String getTradeDate() { return tradeDate; }
        public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }
        public double getCurrentPe() { return currentPe; }
        public void setCurrentPe(double currentPe) { this.currentPe = currentPe; }
        public double getDividendYield() { return dividendYield; }
        public void setDividendYield(double dividendYield) { this.dividendYield = dividendYield; }
        public List<Scenario> getScenarios() { return scenarios; }
        public void setScenarios(List<Scenario> scenarios) { this.scenarios = scenarios; }
        public String getConclusion() { return conclusion; }
        public void setConclusion(String conclusion) { this.conclusion = conclusion; }
    }

    public static class Scenario {
        private String scenario;
        @JsonProperty("scenario_name")
        private String scenarioName;
        @JsonProperty("target_pe")
        private double targetPe;
        @JsonProperty("pe_change_rate")
        private double peChangeRate;
        @JsonProperty("expected_annual_return")
        private double expectedAnnualReturn;
        @JsonProperty("valuation_level")
        private String valuationLevel;
        @JsonProperty("valuation_level_desc")
        private String valuationLevelDesc;
        private String action;
        @JsonProperty("action_desc")
        private String actionDesc;

        public String getScenario() { return scenario; }
        public void setScenario(String scenario) { this.scenario = scenario; }
        public String getScenarioName() { return scenarioName; }
        public void setScenarioName(String scenarioName) { this.scenarioName = scenarioName; }
        public double getTargetPe() { return targetPe; }
        public void setTargetPe(double targetPe) { this.targetPe = targetPe; }
        public double getPeChangeRate() { return peChangeRate; }
        public void setPeChangeRate(double peChangeRate) { this.peChangeRate = peChangeRate; }
        public double getExpectedAnnualReturn() { return expectedAnnualReturn; }
        public void setExpectedAnnualReturn(double expectedAnnualReturn) { this.expectedAnnualReturn = expectedAnnualReturn; }
        public String getValuationLevel() { return valuationLevel; }
        public void setValuationLevel(String valuationLevel) { this.valuationLevel = valuationLevel; }
        public String getValuationLevelDesc() { return valuationLevelDesc; }
        public void setValuationLevelDesc(String valuationLevelDesc) { this.valuationLevelDesc = valuationLevelDesc; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getActionDesc() { return actionDesc; }
        public void setActionDesc(String actionDesc) { this.actionDesc = actionDesc; }
    }
}
