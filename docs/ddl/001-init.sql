CREATE DATABASE IF NOT EXISTS wcinv DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE wcinv;

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-否 1-是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) COMMENT '用户表';

CREATE TABLE IF NOT EXISTS index_valuation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    index_code VARCHAR(20) NOT NULL COMMENT '指数代码',
    index_name VARCHAR(50) NOT NULL COMMENT '指数名称',
    trade_date DATE NOT NULL COMMENT '交易日',
    pe DECIMAL(10,4) COMMENT '市盈率',
    pe_percentile DECIMAL(10,4) COMMENT 'PE 十年分位点',
    pb DECIMAL(10,4) COMMENT '市净率',
    pb_percentile DECIMAL(10,4) COMMENT 'PB 十年分位点',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除 0-否 1-是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_index_date (index_code, trade_date),
    INDEX idx_trade_date (trade_date)
) COMMENT '宽基指数每日估值';
