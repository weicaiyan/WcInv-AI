#!/usr/bin/env python3
"""Fetch index PE/PB data from 理杏仁 Open API and store in MySQL.

Usage:
    python fetch_lixinger.py              # Fetch latest + store
    python fetch_lixinger.py --dry-run    # Print only, no DB write
    python fetch_lixinger.py --full       # Fetch all history from 2015

Token: stored in scripts/lixinger_token.txt (user provides manually)
"""
from __future__ import annotations

import argparse
import json
import logging
import os
import sys
from dataclasses import dataclass
from datetime import date, timedelta
from decimal import ROUND_HALF_UP, Decimal
from pathlib import Path
from typing import Iterable

import requests

LOGGER = logging.getLogger("fetch_lixinger")

API_BASE = "https://open.lixinger.com/api"
INDEX_FUNDAMENTAL = f"{API_BASE}/cn/index/fundamental"

SCRIPT_DIR = Path(__file__).resolve().parent
TOKEN_FILE = SCRIPT_DIR / "lixinger_token.txt"

TARGETS: dict[str, str] = {
    "000300": "沪深300",
    "000905": "中证500",
    "000016": "上证50",
    "399673": "创业板50",
    "399006": "创业板指",
    "399005": "中小板指",
    "000015": "上证红利",
    "000922": "中证红利",
    "399324": "深证红利",
    "000925": "中证基本面50",
    "930782": "中证500低波动",
}

METRICS = [
    "pe_ttm.mcw",
    "pb.mcw",
    "pe_ttm.y10.mcw.cvpos",
    "pb.y10.mcw.cvpos",
    "pe_ttm.y10.mcw.q5v",
    "pb.y10.mcw.q5v",
    "pe_ttm.y10.mcw.q2v",
    "pb.y10.mcw.q2v",
    "pe_ttm.y10.mcw.q8v",
    "pb.y10.mcw.q8v",
    "mc",
]


@dataclass(frozen=True)
class IndexValuation:
    index_code: str
    index_name: str
    trade_date: date
    pe: Decimal
    pe_percentile: Decimal
    pb: Decimal
    pb_percentile: Decimal


def load_token() -> str:
    if TOKEN_FILE.exists():
        return TOKEN_FILE.read_text().strip()
    token = os.getenv("LIXINGER_TOKEN", "")
    if not token:
        raise RuntimeError(
            f"理杏仁 token 未设置。请在 {TOKEN_FILE} 中写入 token，"
            f"或设置环境变量 LIXINGER_TOKEN"
        )
    return token


def save_token(token: str) -> None:
    TOKEN_FILE.write_text(token)


def decimalize(value: object) -> Decimal:
    return Decimal(str(value)).quantize(Decimal("0.0001"), rounding=ROUND_HALF_UP)


def fetch_fundamental(stock_codes: list[str], trade_date: str) -> list[dict]:
    """Call 理杏仁 index fundamental API for one date."""
    payload = {
        "token": load_token(),
        "stockCodes": stock_codes,
        "date": trade_date,
        "metricsList": METRICS,
    }
    r = requests.post(INDEX_FUNDAMENTAL, json=payload, timeout=60)
    data = r.json()
    if data.get("code") != 1:
        raise RuntimeError(f"API error: {json.dumps(data, ensure_ascii=False)[:500]}")
    return data["data"]


def fetch_fundamental_range(stock_code: str, start: str, end: str) -> list[dict]:
    """Call 理杏仁 API for a date range (one stock at a time per API limit)."""
    payload = {
        "token": load_token(),
        "stockCodes": [stock_code],
        "startDate": start,
        "endDate": end,
        "metricsList": METRICS,
        "limit": 5000,
    }
    r = requests.post(INDEX_FUNDAMENTAL, json=payload, timeout=120)
    data = r.json()
    if data.get("code") != 1:
        raise RuntimeError(f"API error for {stock_code}: {json.dumps(data, ensure_ascii=False)[:500]}")
    return data["data"]


def parse_valuation(item: dict, index_name: str) -> IndexValuation:
    """Parse API response item into IndexValuation."""
    trade_date = date.fromisoformat(item["date"][:10])
    pe = decimalize(item.get("pe_ttm.mcw") or 0)
    pb = decimalize(item.get("pb.mcw") or 0)
    mc = decimalize(item.get("mc") or 0)
    # cvpos is 0-1, convert to percentage 0-100
    pe_cvpos = decimalize((item.get("pe_ttm.y10.mcw.cvpos") or 0) * 100)
    pb_cvpos = decimalize((item.get("pb.y10.mcw.cvpos") or 0) * 100)
    # Skip records with zero PE (likely missing data)
    if float(pe) == 0 or float(pb) == 0:
        return None
    return IndexValuation(
        index_code=item["stockCode"],
        index_name=index_name,
        trade_date=trade_date,
        pe=pe,
        pe_percentile=pe_cvpos,
        pb=pb,
        pb_percentile=pb_cvpos,
    )


def get_last_date_in_db(conn, index_code: str) -> date | None:
    """Get the most recent trade_date for an index in the database."""
    with conn.cursor() as cursor:
        cursor.execute(
            "SELECT MAX(trade_date) FROM index_valuation WHERE index_code = %s",
            (index_code,),
        )
        row = cursor.fetchone()
        return row[0] if row and row[0] else None


def upsert_mysql(rows: list[IndexValuation]) -> int:
    """Upsert index valuations into MySQL. Each (index_code, trade_date) is unique."""
    import pymysql

    sql = """
    INSERT INTO index_valuation
        (index_code, index_name, trade_date, pe, pe_percentile, pb, pb_percentile, is_deleted)
    VALUES
        (%s, %s, %s, %s, %s, %s, %s, 0)
    ON DUPLICATE KEY UPDATE
        index_name = VALUES(index_name),
        pe = VALUES(pe),
        pe_percentile = VALUES(pe_percentile),
        pb = VALUES(pb),
        pb_percentile = VALUES(pb_percentile),
        is_deleted = 0,
        update_time = CURRENT_TIMESTAMP
    """
    values = [
        (r.index_code, r.index_name, r.trade_date.isoformat(), r.pe, r.pe_percentile, r.pb, r.pb_percentile)
        for r in rows
    ]
    if not values:
        return 0

    conn = pymysql.connect(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "3306")),
        user=os.getenv("DB_USER", "wcinv"),
        password=os.getenv("DB_PASSWORD", ""),
        database=os.getenv("DB_NAME", "wcinv"),
        charset="utf8mb4",
        autocommit=False,
    )
    try:
        with conn.cursor() as cursor:
            cursor.executemany(sql, values)
        conn.commit()
    finally:
        conn.close()
    return len(values)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Fetch index valuations from 理杏仁")
    parser.add_argument("--dry-run", action="store_true", help="print only, no DB write")
    parser.add_argument("--full", action="store_true", help="fetch all history from 2015")
    parser.add_argument("--symbols", default=",".join(TARGETS), help="comma-separated index codes")
    parser.add_argument("--token", help="save this token to token file")
    return parser.parse_args()


def main() -> int:
    logging.basicConfig(level=logging.INFO, format="%(levelname)s %(message)s")
    args = parse_args()

    if args.token:
        save_token(args.token)
        LOGGER.info("token saved to %s", TOKEN_FILE)

    wanted_codes = {c.strip() for c in args.symbols.split(",") if c.strip()}

    all_rows: list[IndexValuation] = []

    if args.full:
        # Fetch all history for each index
        start = (date.today() - timedelta(days=3650)).isoformat()  # 10 years ago
        end = date.today().isoformat()
        for code, name in TARGETS.items():
            if code not in wanted_codes:
                continue
            LOGGER.info("fetching full history: %s", name)
            items = fetch_fundamental_range(code, start, end)
            count = 0
            for item in items:
                val = parse_valuation(item, name)
                if val:
                    all_rows.append(val)
                    count += 1
            LOGGER.info("  got %d records", count)
    else:
        # Incremental: fetch latest for all indices
        today = date.today()
        # Try today, yesterday, and last Friday
        dates_to_try = []
        for offset in range(5):
            d = today - timedelta(days=offset)
            if d.weekday() < 5:  # weekdays only
                dates_to_try.append(d.isoformat())

        fetched = False
        for dt in dates_to_try:
            try:
                items = fetch_fundamental(list(wanted_codes), dt)
                for code, name in TARGETS.items():
                    if code not in wanted_codes:
                        continue
                    matching = [i for i in items if i["stockCode"] == code]
                    for item in matching:
                        all_rows.append(parse_valuation(item, name))
                fetched = True
                LOGGER.info("fetched data for %s (%d indices)", dt, len(items))
                break
            except RuntimeError:
                LOGGER.info("no data for %s, trying next date", dt)
                continue

        if not fetched:
            LOGGER.error("could not fetch data for any recent trading day")
            return 1

    if not all_rows:
        LOGGER.error("no data fetched")
        return 1

    for row in all_rows:
        LOGGER.info(
            "%s %s %s PE=%s PE%%=%.1f PB=%s PB%%=%.1f",
            row.index_code, row.index_name, row.trade_date,
            row.pe, float(row.pe_percentile), row.pb, float(row.pb_percentile),
        )

    if args.dry_run:
        LOGGER.info("dry-run: skipped MySQL write (%d rows)", len(all_rows))
    else:
        count = upsert_mysql(all_rows)
        LOGGER.info("upserted %d rows", count)

    return 0


if __name__ == "__main__":
    sys.exit(main())
