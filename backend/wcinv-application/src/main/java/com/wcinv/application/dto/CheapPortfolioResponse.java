package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class CheapPortfolioResponse {
    @JsonProperty("trade_date")
    private String tradeDate;
    @JsonProperty("entry_allowed")
    private boolean entryAllowed;
    @JsonProperty("entry_message")
    private String entryMessage;
    @JsonProperty("candidate_count")
    private int candidateCount;
    @JsonProperty("selected_count")
    private int selectedCount;
    @JsonProperty("stocks")
    private List<Stock> stocks = new ArrayList<>();

    public String getTradeDate() { return tradeDate; }
    public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }
    public boolean isEntryAllowed() { return entryAllowed; }
    public void setEntryAllowed(boolean entryAllowed) { this.entryAllowed = entryAllowed; }
    public String getEntryMessage() { return entryMessage; }
    public void setEntryMessage(String entryMessage) { this.entryMessage = entryMessage; }
    public int getCandidateCount() { return candidateCount; }
    public void setCandidateCount(int candidateCount) { this.candidateCount = candidateCount; }
    public int getSelectedCount() { return selectedCount; }
    public void setSelectedCount(int selectedCount) { this.selectedCount = selectedCount; }
    public List<Stock> getStocks() { return stocks; }
    public void setStocks(List<Stock> stocks) { this.stocks = stocks; }

    public static class Stock {
        @JsonProperty("stock_code")
        private String stockCode;
        @JsonProperty("stock_name")
        private String stockName;
        @JsonProperty("industry_name")
        private String industryName;
        @JsonProperty("pe")
        private double pe;
        @JsonProperty("pb")
        private double pb;
        @JsonProperty("dividend_yield")
        private double dividendYield;
        @JsonProperty("price")
        private double price;
        @JsonProperty("pb_percentile_10y")
        private double pbPercentile10y;
        @JsonProperty("composite_rank")
        private int compositeRank;
        @JsonProperty("allocation_ratio")
        private double allocationRatio;

        public String getStockCode() { return stockCode; }
        public void setStockCode(String stockCode) { this.stockCode = stockCode; }
        public String getStockName() { return stockName; }
        public void setStockName(String stockName) { this.stockName = stockName; }
        public String getIndustryName() { return industryName; }
        public void setIndustryName(String industryName) { this.industryName = industryName; }
        public double getPe() { return pe; }
        public void setPe(double pe) { this.pe = pe; }
        public double getPb() { return pb; }
        public void setPb(double pb) { this.pb = pb; }
        public double getDividendYield() { return dividendYield; }
        public void setDividendYield(double dividendYield) { this.dividendYield = dividendYield; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public double getPbPercentile10y() { return pbPercentile10y; }
        public void setPbPercentile10y(double pbPercentile10y) { this.pbPercentile10y = pbPercentile10y; }
        public int getCompositeRank() { return compositeRank; }
        public void setCompositeRank(int compositeRank) { this.compositeRank = compositeRank; }
        public double getAllocationRatio() { return allocationRatio; }
        public void setAllocationRatio(double allocationRatio) { this.allocationRatio = allocationRatio; }
    }
}
