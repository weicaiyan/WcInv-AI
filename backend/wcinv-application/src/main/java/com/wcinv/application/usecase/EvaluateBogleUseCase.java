package com.wcinv.application.usecase;

import com.wcinv.application.dto.BogleResponse;
import com.wcinv.application.port.outbound.BogleRepository;
import com.wcinv.domain.model.BogleScenarioResult;
import com.wcinv.domain.model.BogleSnapshot;
import com.wcinv.domain.service.BogleFormulaCalculator;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 策略02：博格公式估值法。
 */
public class EvaluateBogleUseCase {

    private static final List<String> TARGET_INDEX_CODES = List.of("000932", "000933");
    private static final BigDecimal DEFAULT_GROWTH_RATE = new BigDecimal("0.075");
    private static final int DEFAULT_INVESTMENT_YEARS = 5;

    private final BogleRepository bogleRepository;
    private final BogleFormulaCalculator calculator;

    public EvaluateBogleUseCase(BogleRepository bogleRepository, BogleFormulaCalculator calculator) {
        this.bogleRepository = bogleRepository;
        this.calculator = calculator;
    }

    public BogleResponse execute() {
        List<BogleSnapshot> snapshots = bogleRepository.findLatestSnapshots(TARGET_INDEX_CODES);
        BogleResponse response = new BogleResponse();
        response.setGrowthRate(DEFAULT_GROWTH_RATE.doubleValue());
        response.setInvestmentYears(DEFAULT_INVESTMENT_YEARS);
        response.setTradeDate(snapshots.stream()
                .map(BogleSnapshot::getTradeDate)
                .filter(d -> d != null)
                .max(Comparator.naturalOrder())
                .map(Object::toString)
                .orElse(null));
        response.setIndices(snapshots.stream().map(this::toIndexResult).collect(Collectors.toList()));
        response.setLazyConclusion(lazyConclusion(response.getIndices()));
        return response;
    }

    private BogleResponse.IndexResult toIndexResult(BogleSnapshot snapshot) {
        BogleResponse.IndexResult out = new BogleResponse.IndexResult();
        out.setIndexCode(snapshot.getIndexCode());
        out.setIndexName(snapshot.getIndexName());
        out.setTradeDate(snapshot.getTradeDate() != null ? snapshot.getTradeDate().toString() : null);
        out.setCurrentPe(snapshot.getCurrentPe().doubleValue());
        out.setDividendYield(snapshot.getDividendYield().doubleValue());
        List<BogleScenarioResult> results = calculator.evaluate(snapshot, DEFAULT_GROWTH_RATE, DEFAULT_INVESTMENT_YEARS);
        out.setScenarios(results.stream().map(this::toScenario).collect(Collectors.toList()));
        out.setConclusion(conclusion(snapshot.getIndexName(), results.get(0)));
        return out;
    }

    private BogleResponse.Scenario toScenario(BogleScenarioResult result) {
        BogleResponse.Scenario out = new BogleResponse.Scenario();
        out.setScenario(result.getScenario());
        out.setScenarioName(result.getScenarioName());
        out.setTargetPe(result.getTargetPe().doubleValue());
        out.setPeChangeRate(result.getPeChangeRate().doubleValue());
        out.setExpectedAnnualReturn(result.getExpectedAnnualReturn().doubleValue());
        out.setValuationLevel(result.getValuationLevel().name());
        out.setValuationLevelDesc(result.getValuationLevel().getDescription());
        out.setAction(result.getAction());
        out.setActionDesc(result.getActionDesc());
        return out;
    }

    private String lazyConclusion(List<BogleResponse.IndexResult> indices) {
        if (indices == null || indices.isEmpty()) {
            return "暂无消费/医药指数估值数据。";
        }
        return indices.stream()
                .map(BogleResponse.IndexResult::getConclusion)
                .collect(Collectors.joining("；"));
    }

    private String conclusion(String indexName, BogleScenarioResult conservative) {
        double expectedReturn = conservative.getExpectedAnnualReturn().doubleValue();
        if (expectedReturn > 8.0d) {
            return indexName + "保守年化" + format(expectedReturn) + "% ，可考虑";
        }
        if (expectedReturn < 2.0d) {
            return indexName + "保守年化" + format(expectedReturn) + "% ，先别买";
        }
        return indexName + "保守年化" + format(expectedReturn) + "% ，继续观察";
    }

    private String format(double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }
}
