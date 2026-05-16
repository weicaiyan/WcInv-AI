package com.wcinv.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宽基指数每日估值 - 领域实体。
 */
public class IndexValuation {

    private Long id;
    private String indexCode;
    private String indexName;
    private LocalDate tradeDate;
    private BigDecimal pe;
    private BigDecimal pePercentile;
    private BigDecimal pb;
    private BigDecimal pbPercentile;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getPe() {
        return pe;
    }

    public void setPe(BigDecimal pe) {
        this.pe = pe;
    }

    public BigDecimal getPePercentile() {
        return pePercentile;
    }

    public void setPePercentile(BigDecimal pePercentile) {
        this.pePercentile = pePercentile;
    }

    public BigDecimal getPb() {
        return pb;
    }

    public void setPb(BigDecimal pb) {
        this.pb = pb;
    }

    public BigDecimal getPbPercentile() {
        return pbPercentile;
    }

    public void setPbPercentile(BigDecimal pbPercentile) {
        this.pbPercentile = pbPercentile;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
