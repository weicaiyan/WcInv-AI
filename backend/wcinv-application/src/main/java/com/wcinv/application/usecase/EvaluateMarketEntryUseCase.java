package com.wcinv.application.usecase;

import com.wcinv.application.dto.MarketEntryResponse;
import com.wcinv.application.port.outbound.MarketEntryRepository;
import com.wcinv.domain.model.MarketEntryDecision;
import com.wcinv.domain.model.MarketEntryDecision.MarketIndexSnapshot;
import com.wcinv.domain.service.MarketEntryEvaluator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评估市场入场条件 — 用例。
 */
public class EvaluateMarketEntryUseCase {

    // 策略卡片07要求的两个指数
    private static final List<String> TARGET_INDEX_CODES = List.of("000300", "000905");

    private final MarketEntryRepository marketEntryRepository;
    private final MarketEntryEvaluator evaluator;

    public EvaluateMarketEntryUseCase(MarketEntryRepository marketEntryRepository) {
        this.marketEntryRepository = marketEntryRepository;
        this.evaluator = MarketEntryEvaluator.withDefaults();
    }

    public MarketEntryResponse execute() {
        List<MarketIndexSnapshot> snapshots = marketEntryRepository
                .findLatestByIndexCodes(TARGET_INDEX_CODES);

        MarketEntryDecision decision = evaluator.evaluate(snapshots);

        return toResponse(decision);
    }

    private MarketEntryResponse toResponse(MarketEntryDecision decision) {
        MarketEntryResponse response = new MarketEntryResponse();
        response.setTradeDate(decision.getTradeDate() != null
                ? decision.getTradeDate().toString() : null);
        response.setLevel(decision.getLevel().name());
        response.setLevelDesc(decision.getLevel().getDescription());
        response.setAllowNext(decision.getLevel().isAllowNextStockStrategy());
        response.setMatchedIndexName(decision.getMatchedIndexName());
        response.setWarnings(decision.getWarnings());

        response.setSnapshots(decision.getSnapshots().stream()
                .map(this::toSnapshotResponse)
                .collect(Collectors.toList()));

        return response;
    }

    private MarketEntryResponse.Snapshot toSnapshotResponse(MarketIndexSnapshot snapshot) {
        MarketEntryResponse.Snapshot out = new MarketEntryResponse.Snapshot();
        out.setIndexCode(snapshot.getIndexCode());
        out.setIndexName(snapshot.getIndexName());
        out.setTradeDate(snapshot.getTradeDate() != null
                ? snapshot.getTradeDate().toString() : null);
        out.setPePercentile10y(snapshot.getPePercentile10y() != null
                ? snapshot.getPePercentile10y().doubleValue() : null);
        out.setPbPercentile10y(snapshot.getPbPercentile10y() != null
                ? snapshot.getPbPercentile10y().doubleValue() : null);
        return out;
    }
}