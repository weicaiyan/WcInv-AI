#!/usr/bin/env python3
"""Fetch Bogle formula inputs for consumption/medical indices from 理杏仁."""
import argparse
import logging
import os
from dataclasses import dataclass
from datetime import date
from decimal import Decimal
from pathlib import Path

import pymysql
import requests

LOG = logging.getLogger('bogle_indices')
BASE = 'https://open.lixinger.com/api'
TOKEN_FILE = Path(__file__).with_name('lixinger_token.txt')
DB = dict(
    host=os.getenv('DB_HOST', 'localhost'),
    port=int(os.getenv('DB_PORT', '3306')),
    user=os.getenv('DB_USER', 'wcinv'),
    password=os.getenv('DB_PASSWORD', ''),
    database=os.getenv('DB_NAME', 'wcinv'),
    charset='utf8mb4',
)

TARGETS = {
    '000932': '中证消费',
    '000933': '中证医药',
}
METRICS = [
    'pe_ttm.mcw',
    'dyr.mcw',
    'pe_ttm.y10.mcw.q2v',
    'pe_ttm.y10.mcw.q5v',
    'pe_ttm.y10.mcw.q8v',
]

@dataclass
class BogleIndex:
    trade_date: str
    index_code: str
    index_name: str
    current_pe: Decimal
    dividend_yield: Decimal
    pe_quantile_20: Decimal
    pe_quantile_50: Decimal
    pe_quantile_80: Decimal


def token() -> str:
    return TOKEN_FILE.read_text(encoding='utf-8').strip()


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


def to_decimal(value):
    if value is None:
        return None
    return Decimal(str(value))


def fetch(trade_date: str) -> list[BogleIndex]:
    payload = {
        'token': token(),
        'date': trade_date,
        'stockCodes': list(TARGETS.keys()),
        'metricsList': METRICS,
    }
    resp = requests.post(f'{BASE}/cn/index/fundamental', json=payload, timeout=60)
    data = resp.json()
    if resp.status_code != 200 or data.get('code') != 1:
        raise RuntimeError(f'理杏仁指数接口失败：{resp.status_code} {data}')

    rows = []
    for item in data.get('data', []):
        code = item['stockCode']
        values = [
            to_decimal(item.get('pe_ttm.mcw')),
            to_decimal(item.get('dyr.mcw')),
            to_decimal(item.get('pe_ttm.y10.mcw.q2v')),
            to_decimal(item.get('pe_ttm.y10.mcw.q5v')),
            to_decimal(item.get('pe_ttm.y10.mcw.q8v')),
        ]
        if any(v is None for v in values):
            LOG.warning('skip code=%s missing=%s', code, item)
            continue
        rows.append(BogleIndex(
            trade_date=item.get('date', trade_date)[:10],
            index_code=code,
            index_name=TARGETS.get(code, code),
            current_pe=values[0],
            dividend_yield=values[1],
            pe_quantile_20=values[2],
            pe_quantile_50=values[3],
            pe_quantile_80=values[4],
        ))
    return rows


def create_table(conn):
    with conn.cursor() as c:
        c.execute('''
CREATE TABLE IF NOT EXISTS bogle_index_valuation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  trade_date DATE NOT NULL,
  index_code VARCHAR(16) NOT NULL,
  index_name VARCHAR(64) NOT NULL,
  current_pe DECIMAL(18,4) NOT NULL,
  dividend_yield DECIMAL(18,6) NOT NULL,
  pe_quantile_20 DECIMAL(18,4) NOT NULL,
  pe_quantile_50 DECIMAL(18,4) NOT NULL,
  pe_quantile_80 DECIMAL(18,4) NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_date_code (trade_date, index_code),
  KEY idx_code_date (index_code, trade_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
''')
    conn.commit()


def save(rows: list[BogleIndex]):
    sql = '''
INSERT INTO bogle_index_valuation
(trade_date, index_code, index_name, current_pe, dividend_yield, pe_quantile_20, pe_quantile_50, pe_quantile_80)
VALUES (%s,%s,%s,%s,%s,%s,%s,%s)
ON DUPLICATE KEY UPDATE
 index_name=VALUES(index_name), current_pe=VALUES(current_pe), dividend_yield=VALUES(dividend_yield),
 pe_quantile_20=VALUES(pe_quantile_20), pe_quantile_50=VALUES(pe_quantile_50),
 pe_quantile_80=VALUES(pe_quantile_80), update_time=CURRENT_TIMESTAMP
'''
    conn = pymysql.connect(**DB)
    try:
        create_table(conn)
        with conn.cursor() as c:
            c.executemany(sql, [(r.trade_date, r.index_code, r.index_name, r.current_pe, r.dividend_yield,
                                 r.pe_quantile_20, r.pe_quantile_50, r.pe_quantile_80) for r in rows])
        conn.commit()
    finally:
        conn.close()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--date', default=None)
    parser.add_argument('--dry-run', action='store_true')
    args = parser.parse_args()
    logging.basicConfig(level=logging.INFO, format='%(message)s')
    trade_date = args.date or latest_index_date()
    rows = fetch(trade_date)
    for row in rows:
        LOG.info('%s %s PE=%s DY=%.2f%% Q20=%s Q50=%s Q80=%s',
                 row.index_code, row.index_name, row.current_pe, float(row.dividend_yield * 100),
                 row.pe_quantile_20, row.pe_quantile_50, row.pe_quantile_80)
    if not args.dry_run:
        save(rows)
        LOG.info('saved=%d', len(rows))

if __name__ == '__main__':
    main()
