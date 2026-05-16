# ADR-0005: JWT 无状态认证

**Date**: 2026-05-16
**Status**: accepted
**Deciders**: 寻风, Hermes

## Context

指数温度定投 MVP 需要用户登录后才能使用。需确定认证方案。

## Decision

采用 JWT Bearer Token 无状态认证。

| 项 | 方案 |
|---|------|
| 认证方式 | JWT，`Authorization: Bearer <token>` |
| 密码存储 | BCrypt，cost=12 |
| Session | STATELESS，不创建 session |
| CSRF | 关闭（Bearer token 天然防 CSRF） |
| Token 有效期 | 7 天 |
| Token 刷新 | MVP 不做，过期重新登录 |
| 用户表 | `user`：id, username, password_hash, create_time, update_time |

登录端点：`POST /api/v1/auth/login`
请求：`{ "username": "...", "password": "..." }`
响应：`{ "data": { "token": "...", "expires_in": 604800 } }`

Filter：`JwtAuthFilter extends OncePerRequestFilter`，所有 `/api/**` 拦截，`/api/v1/auth/**` 放行。

## Alternatives Considered

### Session + Cookie
- **Pros**: 服务端可控，Token 可随时撤销
- **Cons**: 需要 session 存储（Redis/内存），前后端分离跨域 cookie 配置麻烦
- **Why not**: 前后端分离 + 手机网页，JWT 更简洁

### OAuth2 / 第三方登录
- **Cons**: MVP 单用户，引入 OAuth2 过度设计
- **Why not**: 后续多用户再加

## Consequences

### Positive
- 无状态，不依赖外部 session 存储
- 前后端分离友好，移动端可直接用

### Negative
- Token 无法主动撤销（MVP 可接受）
- 7 天有效期偏长，后续可加 refresh token

### Risks
- Token 泄露风险 — 前端存 localStorage，XSS 可窃取
  - 缓解：7 天过期限制窗口；后续加 refresh token 缩短 access token 有效期
