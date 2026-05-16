# WcInv — 投资理财软件

## 项目概述
基于9张投资策略卡片，构建投资理财分析工具。MVP优先实现3张核心卡片（PE/PB分位点入场、指数温度定投、便宜组合）。

## 技术栈
- **后端**：Spring Boot + MyBatis-Plus + JDK 11 + Maven + MySQL
- **前端**：Vue 3 + Vant 4，手机优先（Mate 60 Pro），后续可能小程序/App
- **数据源**：AKShare（Python脚本拉取A股行情数据）
- **数据库**：MySQL，用户 overduefree / 密码见本地环境变量或私密配置
- **前后端分离**，后端提供REST API

## 架构：DDD + 六边形架构

### Maven 模块
```
com.wcinv
├── wcinv-shared           公共：异常基类、错误码、工具
├── wcinv-domain           领域：实体、值对象、领域服务（零框架依赖）
├── wcinv-application      应用：端口接口 + 用例实现 + DTO
├── wcinv-infrastructure   出站适配器：MyBatis 持久化、AKShare 客户端
└── wcinv-api              入站适配器：REST Controller + Spring Boot 启动
```

### 依赖方向
```
shared ← domain ← application ← infrastructure
                              ← api
```

### 六边形架构规则
- domain 层零框架依赖，纯 POJO
- application 层定义端口接口（出站端口）+ 用例实现 + 入站端口接口
- infrastructure 实现出站端口（持久化、外部API）
- api 实现入站端口（REST），包含 Spring Boot 启动类和 Bean 装配
- 依赖始终向内

## 数据库
- MySQL，连接信息私密配置
- 表必备三字段：id（bigint）、create_time、update_time（datetime）
- 逻辑删除，不用物理删除
- 命名：is_xxx 表示布尔、字段小写下划线

## Java 编码规范
遵循《阿里巴巴Java开发手册（黄山版）》：
- 类名 UpperCamelCase，方法/变量 lowerCamelCase，常量全大写下划线
- POJO 属性用包装类型，不设默认值
- BigDecimal 用 compareTo 判等
- 禁止魔法值、禁止字符串拼接 SQL
- 日志用 SLF4J，占位符拼接
- 4 空格缩进，单行 ≤120 字符

## 策略卡片
9张策略卡片已审核通过，规则文件见：
- `~/.claude/skills/investment-strategy-01~09.md`
- 知识库：`C:/Users/17890/MyData/knowledge/寻风/03-知识学习/投资理财/策略卡片01-09.md`
- 3个已知缺口（课程图片无法提取）：配比表、周期股行业表、强周期行业表

MVP 优先：07 PE/PB分位点入场 → 01 指数温度定投 → 06 便宜组合

## API 设计规范
- URL：`/api/v1/{资源复数}`，kebab-case，不用动词
- 统一响应格式：`{ data, meta, error: { code, message, details } }`
- 分页：offset-based（MVP），后续可加 cursor-based
- 错误码：字符串，如 "B0001" 系统错误
- POST 创建返回 201 + Location header

## 前端要求
- 手机网页优先，目标设备 Mate 60 Pro
- Vant 4 组件库，移动端适配
- 后续可能迁移小程序/App

## 可用 Agent 工具
- **Claude Code**（主力编码）：`~/.claude/skills/` 下有 228 个 skill
- Hermes 总控：微信交互、知识库管理、定时任务、需求分解
- 可用 skill：`ecc:springboot-*`, `java:alibaba-*`, `sanyuan:code-review-expert`, `design:ui-ux-pro-max`

## 项目路径
- 项目根：`C:\Users\17890\code\WcInv`
- 知识库：`C:\Users\17890\MyData\knowledge\寻风`
