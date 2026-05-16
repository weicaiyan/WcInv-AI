# ADR-0003: REST API 设计规范

**Date**: 2026-05-16
**Status**: accepted
**Deciders**: 寻风, Hermes

## Context

前后端分离，后端需提供 REST API。需确定 URL 风格、响应格式、分页、错误码规范。

## Decision

| 项目 | 规范 | 示例 |
|------|------|------|
| URL | `/api/v1/{资源复数}`，kebab-case，无动词 | `/api/v1/pe-valuations` |
| 响应格式 | `{ data, meta, error }` 统一包装 | `{ "data": {...}, "meta": null, "error": null }` |
| 分页 | offset-based | `?page=0&size=20`，meta 返回 `{ page, size, total }` |
| 错误码 | 阿里规约三段式 | `A0001` 客户端, `B0001` 服务端, `C0001` 第三方 |
| POST | 201 + Location header | `POST /api/v1/indices` → 201, `Location: /api/v1/indices/1` |

## Alternatives Considered

### GraphQL
- **Pros**: 前端按需取字段
- **Cons**: 移动端增加 GraphQL 客户端体积；学习成本；MVP 杀鸡用牛刀
- **Why not**: REST 足够，前端 Vant 4 生态对 REST 友好

### Cursor-based 分页
- **Pros**: 适合实时数据流
- **Cons**: 投资数据更新频率低（日级），offset 够用
- **Why not**: MVP 不引入额外复杂度

## Consequences

### Positive
- 统一格式降低前后端对接成本
- 错误码可定位问题层级
- 后续 API 版本平滑迁移

### Negative
- 简单查询也套一层 `{ data }` 外壳，略冗余

### Risks
- 无
