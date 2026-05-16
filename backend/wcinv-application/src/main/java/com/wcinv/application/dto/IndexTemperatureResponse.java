package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IndexTemperatureResponse {

    @JsonProperty("trade_date")
    private String tradeDate;

    @JsonProperty("pe")
    private Double pe;

    @JsonProperty("pb")
    private Double pb;

    @JsonProperty("index_code")
    private String indexCode;

    @JsonProperty("index_name")
    private String indexName;

    @JsonProperty("pe_temperature")
    private double peTemperature;

    @JsonProperty("pb_temperature")
    private double pbTemperature;

    @JsonProperty("temperature")
    private double temperature;

    @JsonProperty("action")
    private String action;

    @JsonProperty("action_desc")
    private String actionDesc;

    @JsonProperty("ratio")
    private double ratio;

    public IndexTemperatureResponse() {
    }

    public String getTradeDate() { return tradeDate; }
    public void setTradeDate(String tradeDate) { this.tradeDate = tradeDate; }
    public Double getPe() { return pe; }
    public void setPe(Double pe) { this.pe = pe; }
    public Double getPb() { return pb; }
    public void setPb(Double pb) { this.pb = pb; }

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

    public double getPeTemperature() {
        return peTemperature;
    }

    public void setPeTemperature(double peTemperature) {
        this.peTemperature = peTemperature;
    }

    public double getPbTemperature() {
        return pbTemperature;
    }

    public void setPbTemperature(double pbTemperature) {
        this.pbTemperature = pbTemperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionDesc() {
        return actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
