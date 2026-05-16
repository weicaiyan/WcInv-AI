package com.wcinv.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("bogle_index_valuation")
public class BogleIndexValuationEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate tradeDate;
    private String indexCode;
    private String indexName;
    private BigDecimal currentPe;
    private BigDecimal dividendYield;
    private BigDecimal peQuantile20;
    private BigDecimal peQuantile50;
    private BigDecimal peQuantile80;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }
    public String getIndexCode() { return indexCode; }
    public void setIndexCode(String indexCode) { this.indexCode = indexCode; }
    public String getIndexName() { return indexName; }
    public void setIndexName(String indexName) { this.indexName = indexName; }
    public BigDecimal getCurrentPe() { return currentPe; }
    public void setCurrentPe(BigDecimal currentPe) { this.currentPe = currentPe; }
    public BigDecimal getDividendYield() { return dividendYield; }
    public void setDividendYield(BigDecimal dividendYield) { this.dividendYield = dividendYield; }
    public BigDecimal getPeQuantile20() { return peQuantile20; }
    public void setPeQuantile20(BigDecimal peQuantile20) { this.peQuantile20 = peQuantile20; }
    public BigDecimal getPeQuantile50() { return peQuantile50; }
    public void setPeQuantile50(BigDecimal peQuantile50) { this.peQuantile50 = peQuantile50; }
    public BigDecimal getPeQuantile80() { return peQuantile80; }
    public void setPeQuantile80(BigDecimal peQuantile80) { this.peQuantile80 = peQuantile80; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
