package com.wcinv.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 博格公式估值输入快照。
 */
public class BogleSnapshot {

    private final String indexCode;
    private final String indexName;
    private final LocalDate tradeDate;
    private final BigDecimal currentPe;
    private final BigDecimal dividendYield;
    private final BigDecimal peQuantile20;
    private final BigDecimal peQuantile50;
    private final BigDecimal peQuantile80;

    public BogleSnapshot(String indexCode, String indexName, LocalDate tradeDate,
                         BigDecimal currentPe, BigDecimal dividendYield,
                         BigDecimal peQuantile20, BigDecimal peQuantile50, BigDecimal peQuantile80) {
        this.indexCode = indexCode;
        this.indexName = indexName;
        this.tradeDate = tradeDate;
        this.currentPe = currentPe;
        this.dividendYield = dividendYield;
        this.peQuantile20 = peQuantile20;
        this.peQuantile50 = peQuantile50;
        this.peQuantile80 = peQuantile80;
    }

    public String getIndexCode() { return indexCode; }
    public String getIndexName() { return indexName; }
    public LocalDate getTradeDate() { return tradeDate; }
    public BigDecimal getCurrentPe() { return currentPe; }
    public BigDecimal getDividendYield() { return dividendYield; }
    public BigDecimal getPeQuantile20() { return peQuantile20; }
    public BigDecimal getPeQuantile50() { return peQuantile50; }
    public BigDecimal getPeQuantile80() { return peQuantile80; }
}
