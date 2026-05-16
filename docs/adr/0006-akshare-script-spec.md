# ADR-0006: AKShare 数据采集脚本

**Date**: 2026-05-16
**Status**: accepted
**Deciders**: 寻风, Hermes

## Context

指数温度定投需要宽基指数的 PE/PB 及其十年分位点。AKShare 提供这些数据，需编写 Python 脚本定时拉取入库。

## Decision

- 脚本路径：`WcInv/scripts/fetch_index_valuation.py`
- 指数范围：沪深300、中证500、上证50、创业板（4 个）
- 采集字段：PE、PB、PE 十年分位点、PB 十年分位点
- 写入方式：pymysql 直写 `index_valuation` 表
- 运行频率：每日盘后（约 15:30 后）
- 调度方式：Hermes cron job 每天触发

## AKShare 接口

```python
import akshare as ak
# 指数历史估值
df = ak.index_value_hist_funddb(symbol="000300", indicator="市盈率")
```

## Consequences

### Positive
- 与 Java 零耦合
- 脚本可独立测试，不依赖项目编译

### Negative
- 依赖 Python 环境和 AKShare 版本
- 数据时效取决于调度准时性

### Risks
- AKShare 接口变更 → 脚本报错 → 数据断档
  - 缓解：Hermes 监控报警，数据时间戳可检测过期
