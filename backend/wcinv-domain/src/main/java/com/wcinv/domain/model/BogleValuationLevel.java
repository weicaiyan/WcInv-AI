package com.wcinv.domain.model;

/**
 * 博格公式估值判断。
 */
public enum BogleValuationLevel {
    UNDERVALUED("低估"),
    FAIR("正常"),
    OVERVALUED("高估"),
    SEVERE_OVERVALUED("严重高估");

    private final String description;

    BogleValuationLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
