package com.wcinv.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 市场入场决策 — 领域实体。
 */
public class MarketEntryDecision {

    private LocalDate tradeDate;
    private List<MarketIndexSnapshot> snapshots;
    private MarketEntryLevel level;
    private String matchedIndexName;
    private boolean crossIndexMixRejected;
    private List<String> warnings;

    public MarketEntryDecision() {
    }

    public MarketEntryDecision(LocalDate tradeDate, List<MarketIndexSnapshot> snapshots,
                                MarketEntryLevel level, String matchedIndexName,
                                boolean crossIndexMixRejected, List<String> warnings) {
        this.tradeDate = tradeDate;
        this.snapshots = snapshots;
        this.level = level;
        this.matchedIndexName = matchedIndexName;
        this.crossIndexMixRejected = crossIndexMixRejected;
        this.warnings = warnings;
    }

    public LocalDate getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(LocalDate tradeDate) {
        this.tradeDate = tradeDate;
    }

    public List<MarketIndexSnapshot> getSnapshots() {
        return snapshots;
    }

    public void setSnapshots(List<MarketIndexSnapshot> snapshots) {
        this.snapshots = snapshots;
    }

    public MarketEntryLevel getLevel() {
        return level;
    }

    public void setLevel(MarketEntryLevel level) {
        this.level = level;
    }

    public String getMatchedIndexName() {
        return matchedIndexName;
    }

    public void setMatchedIndexName(String matchedIndexName) {
        this.matchedIndexName = matchedIndexName;
    }

    public boolean isCrossIndexMixRejected() {
        return crossIndexMixRejected;
    }

    public void setCrossIndexMixRejected(boolean crossIndexMixRejected) {
        this.crossIndexMixRejected = crossIndexMixRejected;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    // ─── 嵌套值对象 ───

    public static class MarketIndexSnapshot {
        private String indexCode;
        private String indexName;
        private LocalDate tradeDate;
        private BigDecimal pePercentile10y;
        private BigDecimal pbPercentile10y;
        private boolean dataComplete;

        public MarketIndexSnapshot() {
        }

        public MarketIndexSnapshot(String indexCode, String indexName, LocalDate tradeDate,
                                   BigDecimal pePercentile10y, BigDecimal pbPercentile10y) {
            this.indexCode = indexCode;
            this.indexName = indexName;
            this.tradeDate = tradeDate;
            this.pePercentile10y = pePercentile10y;
            this.pbPercentile10y = pbPercentile10y;
            this.dataComplete = pePercentile10y != null && pbPercentile10y != null;
        }

        public String getIndexCode() {
            return indexCode;
        }

        public void setIndexCode(String indexCode) {
            this.indexCode = indexCode;
        }

        public String getIndexName() {
            return indexName;
        }

        public void setIndexName(String indexName) {
            this.indexName = indexName;
        }

        public LocalDate getTradeDate() {
            return tradeDate;
        }

        public void setTradeDate(LocalDate tradeDate) {
            this.tradeDate = tradeDate;
        }

        public BigDecimal getPePercentile10y() {
            return pePercentile10y;
        }

        public void setPePercentile10y(BigDecimal pePercentile10y) {
            this.pePercentile10y = pePercentile10y;
        }

        public BigDecimal getPbPercentile10y() {
            return pbPercentile10y;
        }

        public void setPbPercentile10y(BigDecimal pbPercentile10y) {
            this.pbPercentile10y = pbPercentile10y;
        }

        public boolean isDataComplete() {
            return dataComplete;
        }

        public void setDataComplete(boolean dataComplete) {
            this.dataComplete = dataComplete;
        }
    }
}