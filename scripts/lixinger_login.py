#!/usr/bin/env python3
"""用 playwright 模拟浏览器登录理杏仁，获取 API token。

Usage:
    python lixinger_login.py <account> <password>
    python lixinger_login.py 13648491946 666888

输出 token 到 stdout，异常时 exit(1)。
"""
from __future__ import annotations

import sys
import time
from pathlib import Path

from playwright.sync_api import sync_playwright

SCRIPT_DIR = Path(__file__).resolve().parent
TOKEN_FILE = SCRIPT_DIR / "lixinger_token.txt"
LOGIN_URL = "https://www.lixinger.com/open/api/doc"


def login_and_get_token(account: str, password: str, headless: bool = True) -> str:
    with sync_playwright() as p:
        browser = p.chromium.launch(headless=headless)
        context = browser.new_context(
            user_agent=(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                "AppleWebKit/537.36 (KHTML, like Gecko) "
                "Chrome/120.0.0.0 Safari/537.36"
            ),
            viewport={"width": 1280, "height": 800},
        )
        page = context.new_page()

        try:
            # 1. 打开 API 文档页
            page.goto(LOGIN_URL, wait_until="networkidle", timeout=30000)

            # 2. 点击"登录/注册"触发弹窗
            page.click('a:has-text("登录/注册")')
            time.sleep(1)

            # 3. 等待登录弹窗出现（账号输入框）
            page.wait_for_selector('input[placeholder="账号或手机号"]', timeout=15000)

            # 3. 填表
            page.fill('input[placeholder="账号或手机号"]', account)
            page.fill('input[placeholder="密码"]', password)

            # 4. 点登录按钮（弹窗内有"登录"文字的 button）
            page.click('button:has-text("登录")')

            # 5. 等待登录完成（导航栏显示账号）
            page.wait_for_selector(f'text={account}', timeout=15000)
            time.sleep(2)  # 等 Angular 路由稳定

            # 6. 点击"我的Token"链接
            page.click('a:has-text("我的Token")')
            page.wait_for_load_state("networkidle", timeout=15000)
            time.sleep(2)

            # 7. 提取 token（遍历 input 找含 UUID 格式的值）
            token = ""
            for inp in page.locator("input").all():
                try:
                    val = inp.input_value().strip()
                    # UUID 格式: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
                    if len(val) == 36 and val.count("-") == 4:
                        token = val
                        break
                except Exception:
                    pass

            if not token:
                raise RuntimeError("token 为空")

            # 9. 保存到文件
            TOKEN_FILE.write_text(token)

            return token

        finally:
            browser.close()


def main() -> int:
    if len(sys.argv) != 3:
        print(f"Usage: python {sys.argv[0]} <account> <password>", file=sys.stderr)
        return 1

    account = sys.argv[1]
    password = sys.argv[2]

    try:
        token = login_and_get_token(account, password)
        print(token)
        return 0
    except Exception as e:
        print(f"登录失败: {e}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    sys.exit(main())
