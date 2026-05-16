package com.wcinv.domain.service;

import com.wcinv.domain.model.MarketEntryDecision;
import com.wcinv.domain.model.MarketEntryDecision.MarketIndexSnapshot;
import com.wcinv.domain.model.MarketEntryLevel;
import com.wcinv.domain.model.MarketEntryRule;

import java.util.ArrayList;
import java.util.List;

/**
 * 市场入场评估器 — 领域服务。
 * <p>
 * 核心规则来自策略卡片07：PE/PB分位点估值入场
 * <p>
 * 任意一个指数同时满足 PE≤50% 且 PB≤20% 即 LOW（低估入场）。
 * PE/PB 必须来自同一个指数，不可跨指数拼接。
 */
public class MarketEntryEvaluator {

    private final MarketEntryRule rule;

    public MarketEntryEvaluator(MarketEntryRule rule) {
        this.rule = rule;
    }

    public static MarketEntryEvaluator withDefaults() {
        return new MarketEntryEvaluator(MarketEntryRule.defaults());
    }

    public MarketEntryDecision evaluate(List<MarketIndexSnapshot> snapshots) {
        if (snapshots == null || snapshots.isEmpty()) {
            return new MarketEntryDecision(null, List.of(),
                    MarketEntryLevel.NEUTRAL, null, false,
                    List.of("无估值数据"));
        }

        List<String> warnings = new ArrayList<>();
        boolean anyLow = false;
        String matchedIndexName = null;

        for (MarketIndexSnapshot snapshot : snapshots) {
            if (!snapshot.isDataComplete()) {
                warnings.add(snapshot.getIndexName() + " 数据不完整，跳过");
                continue;
            }

            if (rule.isPeUnder(snapshot.getPePercentile10y())
                    && rule.isPbUnder(snapshot.getPbPercentile10y())) {
                anyLow = true;
                matchedIndexName = snapshot.getIndexName();
                break;
            }
        }

        if (anyLow) {
            warnings.add("低估不代表立刻上涨——2011年9月入场后，市场曾继续下跌一年多才反弹");
            return new MarketEntryDecision(
                    snapshots.get(0).getTradeDate(), snapshots,
                    MarketEntryLevel.LOW, matchedIndexName, false, warnings);
        }

        // 判断是否为高估风险区（PE或PB≥80%）
        boolean inDanger = snapshots.stream()
                .filter(MarketIndexSnapshot::isDataComplete)
                .anyMatch(s -> rule.isInDangerZone(s.getPePercentile10y(), s.getPbPercentile10y()));

        if (inDanger) {
            return new MarketEntryDecision(
                    snapshots.get(0).getTradeDate(), snapshots,
                    MarketEntryLevel.HIGH_RISK, null, false, List.of("PE/PB分位点接近80%危险值"));
        }

        return new MarketEntryDecision(
                snapshots.get(0).getTradeDate(), snapshots,
                MarketEntryLevel.NEUTRAL, null, false, warnings);
    }
}