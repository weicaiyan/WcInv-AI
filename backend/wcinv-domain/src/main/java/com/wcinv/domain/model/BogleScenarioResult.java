package com.wcinv.domain.model;

import java.math.BigDecimal;

/**
 * 博格公式单个情景计算结果。
 */
public class BogleScenarioResult {

    private final String scenario;
    private final String scenarioName;
    private final BigDecimal targetPe;
    private final BigDecimal peChangeRate;
    private final BigDecimal expectedAnnualReturn;
    private final BogleValuationLevel valuationLevel;
    private final String action;
    private final String actionDesc;

    public BogleScenarioResult(String scenario, String scenarioName, BigDecimal targetPe,
                               BigDecimal peChangeRate, BigDecimal expectedAnnualReturn,
                               BogleValuationLevel valuationLevel, String action, String actionDesc) {
        this.scenario = scenario;
        this.scenarioName = scenarioName;
        this.targetPe = targetPe;
        this.peChangeRate = peChangeRate;
        this.expectedAnnualReturn = expectedAnnualReturn;
        this.valuationLevel = valuationLevel;
        this.action = action;
        this.actionDesc = actionDesc;
    }

    public String getScenario() { return scenario; }
    public String getScenarioName() { return scenarioName; }
    public BigDecimal getTargetPe() { return targetPe; }
    public BigDecimal getPeChangeRate() { return peChangeRate; }
    public BigDecimal getExpectedAnnualReturn() { return expectedAnnualReturn; }
    public BogleValuationLevel getValuationLevel() { return valuationLevel; }
    public String getAction() { return action; }
    public String getActionDesc() { return actionDesc; }
}
