# ADR-0001: DDD 六边形架构五模块

**Date**: 2026-05-16
**Status**: accepted
**Deciders**: 寻风, Hermes

## Context

WcInv 是投资理财分析工具，MVP 实现 3 张策略卡片。需确定后端代码组织方式，满足：
- 领域逻辑与框架解耦，方便测试和后续迁移
- 模块职责清晰，新加入的模型能快速理解代码边界
- 支持 AKShare 外部数据源、MyBatis 持久化、REST API 三个方向

## Decision

采用 DDD + 六边形架构，Maven 多模块，5 个模块，依赖方向向内：

```
shared ← domain ← application ← infrastructure
                              ← api
```

| 模块 | 职责 | 依赖 |
|------|------|------|
| wcinv-shared | 异常基类、错误码、工具类 | 无 |
| wcinv-domain | 实体、值对象、领域服务 | shared |
| wcinv-application | 端口接口 + 用例实现 + DTO | domain |
| wcinv-infrastructure | MyBatis 持久化、AKShare 客户端 | domain, application |
| wcinv-api | REST Controller + Spring Boot 启动 | application, infrastructure |

核心规则：
- domain 零框架依赖，纯 POJO
- application 定义出站端口（接口），infrastructure 实现
- api 只做参数校验、调用 application、转换响应

## Alternatives Considered

### 四模块方案（common/data/strategy/api）
- **Pros**: 扁平，模块少
- **Cons**: 业务逻辑散落，领域概念不显式；无端口接口，替换外部依赖需改业务代码
- **Why not**: 无法满足「框架解耦」要求

### 单体 Spring Boot（无多模块）
- **Pros**: 起步快
- **Cons**: 后期边界模糊，代码腐化快
- **Why not**: 不符合 DDD 分层目标

## Consequences

### Positive
- 领域逻辑可单独测试，不依赖 Spring/数据库
- 替换数据源（如 AKShare → 其他 API）只需改 infrastructure
- 模块边界清晰，Claude Code 等 AI agent 按 skill 匹配代码位置

### Negative
- 5 个 POM 模块，Maven 配置稍多
- 小型项目显得「Over-engineering」
- MVP 阶段需手动管理模块间依赖

### Risks
- 过度抽象：MVP 阶段保持务实，领域服务只在必要时抽取
