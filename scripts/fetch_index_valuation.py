#!/usr/bin/env python3
"""Fetch broad-index valuation data from AKShare and upsert into MySQL.

Uses subprocess isolation for each API call to avoid AKShare CSRF token corruption
across multiple calls in the same process.

MVP source: 乐咕乐股 via AKShare stock_index_pe_lg / stock_index_pb_lg.
Percentile rule: latest PE/PB value ranked against the latest 10 years of daily values.
"""
from __future__ import annotations

import argparse
import json
import logging
import os
import subprocess
import sys
import tempfile
import time
from dataclasses import dataclass
from datetime import date, timedelta
from decimal import ROUND_HALF_UP, Decimal
from pathlib import Path
from typing import Iterable

import pandas as pd
import pymysql

LOGGER = logging.getLogger("fetch_index_valuation")

TEN_YEARS = 3650

FETCHER_SCRIPT = """\
import json, sys, traceback
import akshare as ak
import pandas as pd

func_name, symbol = sys.argv[1], sys.argv[2]

try:
    df = getattr(ak, func_name)(symbol=symbol)
    if df is None or df.empty:
        sys.exit(1)
    print(df.to_json(orient="records", date_format="iso", force_ascii=False))
except Exception:
    traceback.print_exc(file=sys.stderr)
    sys.exit(1)
"""


@dataclass(frozen=True)
class IndexTarget:
    code: str
    name: str
    source_symbol: str


@dataclass(frozen=True)
class IndexValuation:
    index_code: str
    index_name: str
    trade_date: date
    pe: Decimal
    pe_percentile: Decimal
    pb: Decimal
    pb_percentile: Decimal


TARGETS: tuple[IndexTarget, ...] = (
    IndexTarget("000300", "沪深300", "沪深300"),
    IndexTarget("000905", "中证500", "中证500"),
    IndexTarget("000016", "上证50", "上证50"),
    IndexTarget("399673", "创业板50", "创业板50"),
)


def decimalize(value: object) -> Decimal:
    return Decimal(str(value)).quantize(Decimal("0.0001"), rounding=ROUND_HALF_UP)


def percentile(series: pd.Series, latest: Decimal) -> Decimal:
    clean = pd.to_numeric(series, errors="coerce").dropna()
    if clean.empty:
        raise ValueError("cannot calculate percentile from empty series")
    ratio = (clean <= float(latest)).sum() / len(clean) * 100
    return decimalize(ratio)


def latest_in_10_years(df: pd.DataFrame) -> pd.DataFrame:
    if "日期" not in df.columns:
        raise ValueError(f"missing 日期 column, got {list(df.columns)}")
    result = df.copy()
    result["日期"] = pd.to_datetime(result["日期"]).dt.date
    result = result.sort_values("日期")
    cutoff = result["日期"].max() - timedelta(days=TEN_YEARS)
    return result[result["日期"] >= cutoff]


def _call_fetcher(func_name: str, symbol: str, attempts: int = 5) -> pd.DataFrame:
    """Call an AKShare function in a subprocess to avoid CSRF token corruption."""
    last_error = ""
    for attempt in range(1, attempts + 1):
        try:
            proc = subprocess.run(
                [sys.executable, "-c", FETCHER_SCRIPT, func_name, symbol],
                capture_output=True,
                text=True,
                timeout=300,
            )
            if proc.returncode == 0 and proc.stdout.strip():
                df = pd.read_json(proc.stdout.strip(), orient="records", convert_dates=["日期"])
                if not df.empty:
                    return df
            last_error = proc.stderr.strip()[-200:] or f"exit {proc.returncode}, no stdout"
        except (subprocess.TimeoutExpired, Exception) as exc:
            last_error = str(exc)[:200]
        if attempt < attempts:
            time.sleep(attempt * 5)
    raise RuntimeError(f"subprocess fetch failed after {attempts} attempts: {last_error}")


def fetch_one(target: IndexTarget) -> IndexValuation:
    LOGGER.info("fetching %s", target.name)

    pe_df = latest_in_10_years(_call_fetcher("stock_index_pe_lg", target.source_symbol))
    pb_df = latest_in_10_years(_call_fetcher("stock_index_pb_lg", target.source_symbol))

    if pe_df.empty or pb_df.empty:
        raise ValueError(f"empty valuation data for {target.name}")
    if "滚动市盈率" not in pe_df.columns:
        raise ValueError(f"missing 滚动市盈率 column for {target.name}")
    if "市净率" not in pb_df.columns:
        raise ValueError(f"missing 市净率 column for {target.name}")

    latest_pe_row = pe_df.iloc[-1]
    trade_date = latest_pe_row["日期"]
    pb_same_day = pb_df[pb_df["日期"] == trade_date]
    latest_pb_row = pb_same_day.iloc[-1] if not pb_same_day.empty else pb_df.iloc[-1]

    pe = decimalize(latest_pe_row["滚动市盈率"])
    pb = decimalize(latest_pb_row["市净率"])

    return IndexValuation(
        index_code=target.code,
        index_name=target.name,
        trade_date=trade_date,
        pe=pe,
        pe_percentile=percentile(pe_df["滚动市盈率"], pe),
        pb=pb,
        pb_percentile=percentile(pb_df["市净率"], pb),
    )


def connect_mysql():
    return pymysql.connect(
        host=os.getenv("DB_HOST", "localhost"),
        port=int(os.getenv("DB_PORT", "3306")),
        user=os.getenv("DB_USER", "wcinv"),
        password=os.getenv("DB_PASSWORD", ""),
        database=os.getenv("DB_NAME", "wcinv"),
        charset="utf8mb4",
        autocommit=False,
    )


def upsert(rows: Iterable[IndexValuation]) -> int:
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
        (
            row.index_code,
            row.index_name,
            row.trade_date.isoformat(),
            row.pe,
            row.pe_percentile,
            row.pb,
            row.pb_percentile,
        )
        for row in rows
    ]
    if not values:
        return 0

    with connect_mysql() as conn:
        with conn.cursor() as cursor:
            cursor.executemany(sql, values)
        conn.commit()
    return len(values)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Fetch AKShare index valuations into MySQL")
    parser.add_argument("--dry-run", action="store_true", help="print fetched rows without writing MySQL")
    parser.add_argument(
        "--strict",
        action="store_true",
        help="exit non-zero if any target index fails; default keeps successful rows",
    )
    parser.add_argument(
        "--symbols",
        default=",".join(target.code for target in TARGETS),
        help="comma-separated index codes to fetch; default all MVP targets",
    )
    return parser.parse_args()


def main() -> int:
    logging.basicConfig(level=logging.INFO, format="%(levelname)s %(message)s")
    args = parse_args()
    wanted_codes = {code.strip() for code in args.symbols.split(",") if code.strip()}
    targets = [target for target in TARGETS if target.code in wanted_codes]

    rows: list[IndexValuation] = []
    failures: list[str] = []

    for target in targets:
        try:
            rows.append(fetch_one(target))
        except Exception as exc:  # noqa: BLE001
            message = f"{target.name}({target.code}) failed: {exc}"
            failures.append(message)
            LOGGER.warning(message)
        time.sleep(2)

    if not rows:
        LOGGER.error("no valuation rows fetched")
        return 1

    for row in rows:
        LOGGER.info(
            "%s %s PE=%s PE%%=%s PB=%s PB%%=%s date=%s",
            row.index_code,
            row.index_name,
            row.pe,
            row.pe_percentile,
            row.pb,
            row.pb_percentile,
            row.trade_date,
        )

    if args.dry_run:
        LOGGER.info("dry-run: skipped MySQL write")
    else:
        count = upsert(rows)
        LOGGER.info("upserted %s rows into index_valuation", count)

    if failures and args.strict:
        return 2
    return 0


if __name__ == "__main__":
    sys.exit(main())
