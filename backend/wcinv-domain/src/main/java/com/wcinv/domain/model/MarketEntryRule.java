package com.wcinv.domain.model;

import java.math.BigDecimal;

/**
 * 市场入场规则 — 值对象。
 * <p>
 * 规则来源：策略卡片07 PE/PB分位点估值入场
 */
public class MarketEntryRule {

    public static final int DEFAULT_LOOKBACK_YEARS = 10;
    public static final BigDecimal DEFAULT_PE_PERCENTILE_MAX = new BigDecimal("50");
    public static final BigDecimal DEFAULT_PB_PERCENTILE_MAX = new BigDecimal("20");
    public static final BigDecimal DANGER_ZONE_THRESHOLD = new BigDecimal("80");

    private final BigDecimal pePercentileMax;
    private final BigDecimal pbPercentileMax;
    private final int lookbackYears;
    private final boolean allowAnyIndexMatched;

    MarketEntryRule(BigDecimal pePercentileMax, BigDecimal pbPercentileMax,
                    int lookbackYears, boolean allowAnyIndexMatched) {
        this.pePercentileMax = pePercentileMax;
        this.pbPercentileMax = pbPercentileMax;
        this.lookbackYears = lookbackYears;
        this.allowAnyIndexMatched = allowAnyIndexMatched;
    }

    public static MarketEntryRule defaults() {
        return new MarketEntryRule(DEFAULT_PE_PERCENTILE_MAX, DEFAULT_PB_PERCENTILE_MAX,
                DEFAULT_LOOKBACK_YEARS, true);
    }

    public boolean isPeUnder(BigDecimal pePercentile) {
        return pePercentile != null && pePercentile.compareTo(pePercentileMax) <= 0;
    }

    public boolean isPbUnder(BigDecimal pbPercentile) {
        return pbPercentile != null && pbPercentile.compareTo(pbPercentileMax) <= 0;
    }

    public boolean isInDangerZone(BigDecimal pePercentile, BigDecimal pbPercentile) {
        BigDecimal maxPct = max(pePercentile, pbPercentile);
        return maxPct != null && maxPct.compareTo(DANGER_ZONE_THRESHOLD) >= 0;
    }

    public boolean isAllowAnyIndexMatched() {
        return allowAnyIndexMatched;
    }

    public BigDecimal getPePercentileMax() {
        return pePercentileMax;
    }

    public BigDecimal getPbPercentileMax() {
        return pbPercentileMax;
    }

    public int getLookbackYears() {
        return lookbackYears;
    }

    private static BigDecimal max(BigDecimal a, BigDecimal b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.compareTo(b) >= 0 ? a : b;
    }
}