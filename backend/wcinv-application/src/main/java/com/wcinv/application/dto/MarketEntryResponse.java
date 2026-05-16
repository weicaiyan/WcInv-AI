package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class MarketEntryResponse {

    @JsonProperty("trade_date")
    private String tradeDate;

    @JsonProperty("level")
    private String level;

    @JsonProperty("level_desc")
    private String levelDesc;

    @JsonProperty("allow_next")
    private boolean allowNext;

    @JsonProperty("matched_index_name")
    private String matchedIndexName;

    @JsonProperty("snapshots")
    private List<Snapshot> snapshots = new ArrayList<>();

    @JsonProperty("warnings")
    private List<String> warnings = new ArrayList<>();

    // getters/setters...

    public String getTradeDate() { return tradeDate; }
    public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getLevelDesc() { return levelDesc; }
    public void setLevelDesc(String levelDesc) { this.levelDesc = levelDesc; }
    public boolean isAllowNext() { return allowNext; }
    public void setAllowNext(boolean allowNext) { this.allowNext = allowNext; }
    public String getMatchedIndexName() { return matchedIndexName; }
    public void setMatchedIndexName(String matchedIndexName) { this.matchedIndexName = matchedIndexName; }
    public List<Snapshot> getSnapshots() { return snapshots; }
    public void setSnapshots(List<Snapshot> snapshots) { this.snapshots = snapshots; }
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }

    public static class Snapshot {
        @JsonProperty("index_code")
        private String indexCode;
        @JsonProperty("index_name")
        private String indexName;
        @JsonProperty("trade_date")
        private String tradeDate;
        @JsonProperty("pe_percentile_10y")
        private Double pePercentile10y;
        @JsonProperty("pb_percentile_10y")
        private Double pbPercentile10y;

        public String getIndexCode() { return indexCode; }
        public void setIndexCode(String indexCode) { this.indexCode = indexCode; }
        public String getIndexName() { return indexName; }
        public void setIndexName(String indexName) { this.indexName = indexName; }
        public String getTradeDate() { return tradeDate; }
        public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }
        public Double getPePercentile10y() { return pePercentile10y; }
        public void setPePercentile10y(Double pePercentile10y) { this.pePercentile10y = pePercentile10y; }
        public Double getPbPercentile10y() { return pbPercentile10y; }
        public void setPbPercentile10y(Double pbPercentile10y) { this.pbPercentile10y = pbPercentile10y; }
    }
}