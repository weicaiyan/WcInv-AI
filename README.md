# WcInv 项目结构

当前采用前后端分离，但统一放在 `C:\Users\17890\code\WcInv` 下。

## 目录

- `frontend/`：Vue 3 + Vant 4 手机端前端。
- `backend/`：Spring Boot + MyBatis-Plus 后端 Maven 多模块。
- `scripts/`：Python AKShare 数据采集脚本。
- `docs/`：ADR、DDL、项目文档。
- `start-frontend.bat`：双击重启前端，端口 5173。
- `start-backend.bat`：双击重启后端，端口 8080。

## 常用启动

1. 准备 MySQL 数据库 `wcinv`，并按 `.env.example` 设置本地环境变量；也可以复制成不提交的 `.env.local`，`start-backend.bat` 会自动读取。
2. 理杏仁数据脚本需要设置 `LIXINGER_TOKEN`，或在本地创建不提交的 `scripts/lixinger_token.txt`。
3. 双击 `start-backend.bat` 启动后端。
4. 双击 `start-frontend.bat` 启动前端。
5. 浏览器打开 `http://localhost:5173`。
6. 登录账号：`admin / 123456`。

## 命令行验证

- 后端测试：在 `backend/` 下运行 `mvn test`。
- 前端构建：在 `frontend/` 下运行 `npm run build`。

## Git 分支

- `master`：只放已验证、可运行、可上线的代码。
- 新需求/修复从 `master` 拉新分支开发，验证通过后再合并回 `master`。
- 详细规范见 `docs/BRANCHING.md`。
