# ADR-0002: AKShare Python 脚本独立运行 + 直写 MySQL

**Date**: 2026-05-16
**Status**: accepted
**Deciders**: 寻风, Hermes

## Context

AKShare 是 Python 库，拉取 A 股 PE/PB 分位点、指数估值等数据。Java 后端不能直接 import Python 库。需确定 Python 脚本如何与 Java 后端协作。

## Decision

Python 脚本放在 `WcInv/scripts/` 独立目录，独立运行（定时任务或手动），通过 pymysql 直接写 MySQL。Java 后端只从 MySQL 读取数据，不感知 Python 脚本的存在。

脚本位置：`WcInv/scripts/`
调用方式：Python 独立运行 → pymysql → MySQL ← MyBatis ← Java

## Alternatives Considered

### B: 脚本随 infrastructure 模块（`wcinv-infrastructure/src/main/python/`）
- **Pros**: 打包一致，部署统一
- **Cons**: Java 需 ProcessBuilder 管理 Python 进程、超时、重试、Python 环境依赖；调试困难；耦合重
- **Why not**: MVP 阶段引入进程管理增加复杂度，解耦优于集成

### 纯 Java 重写数据拉取（不用 Python）
- **Pros**: 技术栈统一
- **Cons**: Java 无 AKShare 等价库，需手写 HTTP 爬虫，维护成本高
- **Why not**: AKShare 封装了大量数据源，重写不划算

## Consequences

### Positive
- 脚本和 Java 完全解耦，各自独立调度/排查
- 脚本可本地手动运行调试，不依赖 Java 项目编译
- 符合六边形架构：AKShare 是外部出站适配器，通过数据库桥接

### Negative
- 前后端之外多一个运行组件（Python 脚本）
- 脚本需单独部署/调度，运维点多一个
- 脚本失败不影响 Java 运行，但数据可能过期

### Risks
- 数据时效性：脚本未按时运行 → MySQL 数据过旧 → 前端展示过期数据
  - 缓解：后续加数据时间戳、前端显示数据更新时间；Hermes 定时任务监控
