#!/usr/bin/env python3
"""Fetch cheap stock candidates from 理杏仁 Open API.

MVP scope: A-share non-financial companies via cn/company/fundamental/non_financial.
"""
import argparse
import json
import logging
import math
import os
import time
from dataclasses import dataclass
from datetime import date
from decimal import Decimal
from pathlib import Path
from typing import Iterable

import pymysql
import requests

LOG = logging.getLogger('cheap_stocks')
BASE = 'https://open.lixinger.com/api'
TOKEN_FILE = Path(__file__).with_name('lixinger_token.txt')
METRICS = ['d_pe_ttm', 'pb_wo_gw', 'dyr', 'sp', 'pb_wo_gw.y10.cvpos']

DB = dict(
    host=os.getenv('DB_HOST', 'localhost'),
    port=int(os.getenv('DB_PORT', '3306')),
    user=os.getenv('DB_USER', 'wcinv'),
    password=os.getenv('DB_PASSWORD', ''),
    database=os.getenv('DB_NAME', 'wcinv'),
    charset='utf8mb4',
)

@dataclass
class Candidate:
    trade_date: str
    stock_code: str
    stock_name: str
    industry_name: str
    pe: Decimal
    pb: Decimal
    dividend_yield: Decimal
    price: Decimal
    pb_percentile_10y: Decimal
    pe_rank: int = 0
    pb_rank: int = 0
    dividend_rank: int = 0
    composite_rank: int = 0
    selected: bool = False
    allocation_ratio: Decimal = Decimal('0')


def token() -> str:
    return TOKEN_FILE.read_text(encoding='utf-8').strip()


def post(path: str, payload: dict) -> dict:
    payload = {'token': token(), **payload}
    r = requests.post(f'{BASE}/{path}', json=payload, timeout=30)
    data = r.json()
    if r.status_code != 200 or data.get('code') != 1:
        raise RuntimeError(f'{path} failed: {r.status_code} {data}')
    return data


def chunks(xs: list, n: int) -> Iterable[list]:
    for i in range(0, len(xs), n):
        yield xs[i:i+n]


def latest_index_date() -> str:
    conn = pymysql.connect(**DB)
    try:
        with conn.cursor() as c:
            c.execute('SELECT MAX(trade_date) FROM index_valuation')
            row = c.fetchone()
            if row and row[0]:
                return row[0].isoformat()
    finally:
        conn.close()
    return date.today().isoformat()


def create_table(conn):
    with conn.cursor() as c:
        c.execute('''
CREATE TABLE IF NOT EXISTS cheap_stock_candidate (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  trade_date DATE NOT NULL,
  stock_code VARCHAR(16) NOT NULL,
  stock_name VARCHAR(64) NOT NULL,
  industry_name VARCHAR(64),
  pe_ttm_deducted DECIMAL(18,4) NOT NULL,
  pb_without_goodwill DECIMAL(18,4) NOT NULL,
  dividend_yield DECIMAL(18,6) NOT NULL,
  price DECIMAL(18,4) NOT NULL,
  pb_percentile_10y DECIMAL(18,6) NOT NULL,
  pe_rank INT NOT NULL,
  pb_rank INT NOT NULL,
  dividend_rank INT NOT NULL,
  composite_rank INT NOT NULL,
  selected TINYINT(1) NOT NULL DEFAULT 0,
  allocation_ratio DECIMAL(18,6) NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_date_code (trade_date, stock_code),
  KEY idx_date_selected (trade_date, selected),
  KEY idx_date_rank (trade_date, composite_rank)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
''')
    conn.commit()


def get_companies() -> list[dict]:
    data = post('cn/company', {})['data']
    # A股沪深两市，非金融；北交所先不纳入MVP
    return [x for x in data if x.get('market') == 'a' and x.get('exchange') in ('sh', 'sz') and x.get('fsTableType') == 'non_financial']


def fetch_fundamentals(codes: list[str], trade_date: str) -> list[dict]:
    all_items = []
    for batch in chunks(codes, 100):
        data = post('cn/company/fundamental/non_financial', {
            'date': trade_date,
            'stockCodes': batch,
            'metricsList': METRICS,
        })
        all_items.extend(data.get('data', []))
        time.sleep(0.15)
    return all_items


def fetch_industry(stock_code: str) -> str:
    try:
        items = post('cn/company/industries', {'stockCode': stock_code}).get('data', [])
        # 优先申万2021二级/一级，退化到第一个
        sw = [x for x in items if x.get('source') == 'sw_2021']
        if sw:
            return sw[-1].get('name') or sw[0].get('name') or '未知'
        return items[-1].get('name') if items else '未知'
    except Exception:
        return '未知'


def to_decimal(v):
    if v is None:
        return None
    return Decimal(str(v))


def rank(items: list[Candidate]):
    for i, x in enumerate(sorted(items, key=lambda c: (c.pe, c.stock_code)), 1):
        x.pe_rank = i
    for i, x in enumerate(sorted(items, key=lambda c: (c.pb, c.stock_code)), 1):
        x.pb_rank = i
    for i, x in enumerate(sorted(items, key=lambda c: (-c.dividend_yield, c.stock_code)), 1):
        x.dividend_rank = i
    for x in items:
        x.composite_rank = x.pe_rank + x.pb_rank + x.dividend_rank


def select_portfolio(items: list[Candidate], target=10) -> list[Candidate]:
    max_same_industry = max(1, math.floor(target * 0.30))
    counts = {}
    selected = []
    for x in sorted(items, key=lambda c: (c.composite_rank, c.pe, c.pb, -c.dividend_yield, c.stock_code)):
        if len(selected) >= target:
            break
        ind = x.industry_name or '未知'
        if counts.get(ind, 0) >= max_same_industry:
            continue
        selected.append(x)
        counts[ind] = counts.get(ind, 0) + 1
    if selected:
        allocation = Decimal('1') / Decimal(len(selected))
        for x in selected:
            x.selected = True
            x.allocation_ratio = allocation
    return selected


def save(conn, items: list[Candidate], trade_date: str):
    sql = '''
INSERT INTO cheap_stock_candidate
(trade_date, stock_code, stock_name, industry_name, pe_ttm_deducted, pb_without_goodwill,
 dividend_yield, price, pb_percentile_10y, pe_rank, pb_rank, dividend_rank, composite_rank,
 selected, allocation_ratio)
VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)
ON DUPLICATE KEY UPDATE
 stock_name=VALUES(stock_name), industry_name=VALUES(industry_name), pe_ttm_deducted=VALUES(pe_ttm_deducted),
 pb_without_goodwill=VALUES(pb_without_goodwill), dividend_yield=VALUES(dividend_yield), price=VALUES(price),
 pb_percentile_10y=VALUES(pb_percentile_10y), pe_rank=VALUES(pe_rank), pb_rank=VALUES(pb_rank),
 dividend_rank=VALUES(dividend_rank), composite_rank=VALUES(composite_rank), selected=VALUES(selected),
 allocation_ratio=VALUES(allocation_ratio), update_time=CURRENT_TIMESTAMP
'''
    rows = [(x.trade_date, x.stock_code, x.stock_name, x.industry_name, x.pe, x.pb, x.dividend_yield,
             x.price, x.pb_percentile_10y, x.pe_rank, x.pb_rank, x.dividend_rank, x.composite_rank,
             1 if x.selected else 0, x.allocation_ratio) for x in items]
    with conn.cursor() as c:
        c.execute('DELETE FROM cheap_stock_candidate WHERE trade_date=%s', (trade_date,))
        if rows:
            c.executemany(sql, rows)
    conn.commit()


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--date', default=None, help='交易日；默认使用index_valuation最新交易日')
    ap.add_argument('--dry-run', action='store_true')
    args = ap.parse_args()
    trade_date = args.date or latest_index_date()
    logging.basicConfig(level=logging.INFO, format='%(message)s')

    companies = get_companies()
    name_map = {x['stockCode']: x.get('name', '') for x in companies}
    LOG.info('date=%s companies=%d', trade_date, len(companies))

    raw = fetch_fundamentals(list(name_map.keys()), trade_date)
    candidates = []
    for item in raw:
        pe = to_decimal(item.get('d_pe_ttm'))
        pb = to_decimal(item.get('pb_wo_gw'))
        dyr = to_decimal(item.get('dyr'))
        sp = to_decimal(item.get('sp'))
        pbp = to_decimal(item.get('pb_wo_gw.y10.cvpos'))
        if None in (pe, pb, dyr, sp, pbp):
            continue
        # 策略06初筛：0<=PE<=10；0<=PB<=1.5；股息率>=3%；PB十年分位<20%
        if not (Decimal('0') <= pe <= Decimal('10')):
            continue
        if not (Decimal('0') <= pb <= Decimal('1.5')):
            continue
        if dyr < Decimal('0.03'):
            continue
        if pbp >= Decimal('0.20'):
            continue
        code = item['stockCode']
        candidates.append(Candidate(trade_date, code, name_map.get(code, ''), '未知', pe, pb, dyr, sp, pbp))

    rank(candidates)
    LOG.info('candidates=%d', len(candidates))
    # 只给候选补行业，减少API调用
    for i, c in enumerate(candidates, 1):
        c.industry_name = fetch_industry(c.stock_code)
        if i % 20 == 0:
            LOG.info('industries %d/%d', i, len(candidates))
        time.sleep(0.05)
    # 补行业后重选，确保行业30%约束生效
    for c in candidates:
        c.selected = False
        c.allocation_ratio = Decimal('0')
    selected = select_portfolio(candidates)
    LOG.info('selected=%d', len(selected))

    for c in selected:
        LOG.info('SELECT %s %s %s PE=%s PB=%s DY=%.2f%% PBP=%.2f%% rank=%s alloc=%.1f%%',
                 c.stock_code, c.stock_name, c.industry_name, c.pe, c.pb,
                 float(c.dividend_yield * 100), float(c.pb_percentile_10y * 100),
                 c.composite_rank, float(c.allocation_ratio * 100))

    if not args.dry_run:
        conn = pymysql.connect(**DB)
        create_table(conn)
        save(conn, candidates, trade_date)
        conn.close()
        LOG.info('saved=%d', len(candidates))

if __name__ == '__main__':
    main()
