# Git 分支规范

## 主分支

- `master`：主分支，只存放已验证、可运行、可上线的代码。
- 不在 `master` 上直接开发需求或修 bug。

## 开发流程

1. 从最新 `master` 拉取新分支：
   - 新需求：`feature/xxx`
   - Bug 修复：`fix/xxx`
   - 重构：`refactor/xxx`
2. 在分支上开发并本地验证。
3. 提交 Pull Request 到 `master`。
4. 测试通过、确认没问题后再合并到 `master`。
5. 合并后删除临时分支。

## 本地命令示例

```bash
git checkout master
git pull origin master
git checkout -b feature/bogle-formula

# 开发、测试通过后
git add .
git commit -m "feat: add bogle formula strategy"
git push -u origin feature/bogle-formula
```

## 合并前检查

- 后端：`cd backend && mvn test`
- 前端：`cd frontend && npm run build`
- 确认没有提交 `.env`、token、cookie、数据库密码等敏感信息。
