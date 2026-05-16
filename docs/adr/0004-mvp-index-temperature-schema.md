# ADR-0004: MVP 数据库从指数温度开始，单表先上线

**Date**: 2026-05-16
**Status**: accepted
**Deciders**: 寻风, Hermes

## Context

MVP 原计划 3 张卡片（07 PE/PB入场 + 01 指数温度 + 06 便宜组合），现决定收缩为单张：**01 指数温度定投**。数据库只需支撑这一张卡。

## Decision

MVP 建 1 张表 `index_valuation`，存储宽基指数每日估值快照。温度计算在 Java 应用层实时完成，不落表。

```sql
CREATE TABLE index_valuation (
    id              BIGINT          PRIMARY KEY AUTO_INCREMENT,
    index_code      VARCHAR(20)     NOT NULL COMMENT '指数代码，如 000300',
    index_name      VARCHAR(50)     NOT NULL COMMENT '指数名称',
    trade_date      DATE            NOT NULL COMMENT '交易日',
    pe              DECIMAL(10,4)   COMMENT '市盈率',
    pe_percentile   DECIMAL(10,4)   COMMENT 'PE 十年分位点 (0-100)',
    pb              DECIMAL(10,4)   COMMENT '市净率',
    pb_percentile   DECIMAL(10,4)   COMMENT 'PB 十年分位点 (0-100)',
    create_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_index_date (index_code, trade_date),
    INDEX idx_trade_date (trade_date)
) COMMENT '宽基指数每日估值';
```

Java 计算：`温度 = (pe_percentile + pb_percentile) / 2`，查系数表返回定投建议。

## Consequences

### Positive
- 1 张表起步，schema 变更零成本
- 温度计算规则无状态，改系数表不改 SQL
- 后续加策略卡片时加表，不影响现有

### Negative
- 后续加个股估值需新表，但那是后续的事

### Risks
- 无
