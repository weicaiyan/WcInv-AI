package com.wcinv.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("cheap_stock_candidate")
public class CheapStockCandidateEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate tradeDate;
    private String stockCode;
    private String stockName;
    private String industryName;
    private BigDecimal peTtmDeducted;
    private BigDecimal pbWithoutGoodwill;
    private BigDecimal dividendYield;
    private BigDecimal price;
    @TableField("pb_percentile_10y")
    private BigDecimal pbPercentile10y;
    private Integer peRank;
    private Integer pbRank;
    private Integer dividendRank;
    private Integer compositeRank;
    private Boolean selected;
    private BigDecimal allocationRatio;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getTradeDate() { return tradeDate; }
    public void setTradeDate(LocalDate tradeDate) { this.tradeDate = tradeDate; }
    public String getStockCode() { return stockCode; }
    public void setStockCode(String stockCode) { this.stockCode = stockCode; }
    public String getStockName() { return stockName; }
    public void setStockName(String stockName) { this.stockName = stockName; }
    public String getIndustryName() { return industryName; }
    public void setIndustryName(String industryName) { this.industryName = industryName; }
    public BigDecimal getPeTtmDeducted() { return peTtmDeducted; }
    public void setPeTtmDeducted(BigDecimal peTtmDeducted) { this.peTtmDeducted = peTtmDeducted; }
    public BigDecimal getPbWithoutGoodwill() { return pbWithoutGoodwill; }
    public void setPbWithoutGoodwill(BigDecimal pbWithoutGoodwill) { this.pbWithoutGoodwill = pbWithoutGoodwill; }
    public BigDecimal getDividendYield() { return dividendYield; }
    public void setDividendYield(BigDecimal dividendYield) { this.dividendYield = dividendYield; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getPbPercentile10y() { return pbPercentile10y; }
    public void setPbPercentile10y(BigDecimal pbPercentile10y) { this.pbPercentile10y = pbPercentile10y; }
    public Integer getPeRank() { return peRank; }
    public void setPeRank(Integer peRank) { this.peRank = peRank; }
    public Integer getPbRank() { return pbRank; }
    public void setPbRank(Integer pbRank) { this.pbRank = pbRank; }
    public Integer getDividendRank() { return dividendRank; }
    public void setDividendRank(Integer dividendRank) { this.dividendRank = dividendRank; }
    public Integer getCompositeRank() { return compositeRank; }
    public void setCompositeRank(Integer compositeRank) { this.compositeRank = compositeRank; }
    public Boolean getSelected() { return selected; }
    public void setSelected(Boolean selected) { this.selected = selected; }
    public BigDecimal getAllocationRatio() { return allocationRatio; }
    public void setAllocationRatio(BigDecimal allocationRatio) { this.allocationRatio = allocationRatio; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
