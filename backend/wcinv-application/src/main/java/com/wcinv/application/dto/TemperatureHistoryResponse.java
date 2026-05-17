package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class TemperatureHistoryResponse {

    @JsonProperty("index_code")
    private String indexCode;

    @JsonProperty("index_name")
    private String indexName;

    @JsonProperty("history")
    private List<HistoryItem> history;

    public String getIndexCode() { return indexCode; }
    public void setIndexCode(String indexCode) { this.indexCode = indexCode; }

    public String getIndexName() { return indexName; }
    public void setIndexName(String indexName) { this.indexName = indexName; }

    public List<HistoryItem> getHistory() { return history; }
    public void setHistory(List<HistoryItem> history) { this.history = history; }

    public static class HistoryItem {

        @JsonProperty("date")
        private String date;

        @JsonProperty("pe_temp")
        private BigDecimal peTemp;

        @JsonProperty("pb_temp")
        private BigDecimal pbTemp;

        @JsonProperty("temperature")
        private BigDecimal temperature;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public BigDecimal getPeTemp() { return peTemp; }
        public void setPeTemp(BigDecimal peTemp) { this.peTemp = peTemp; }

        public BigDecimal getPbTemp() { return pbTemp; }
        public void setPbTemp(BigDecimal pbTemp) { this.pbTemp = pbTemp; }

        public BigDecimal getTemperature() { return temperature; }
        public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
    }
}
