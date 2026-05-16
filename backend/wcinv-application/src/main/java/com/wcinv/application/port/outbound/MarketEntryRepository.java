package com.wcinv.application.port.outbound;

import com.wcinv.domain.model.MarketEntryDecision.MarketIndexSnapshot;

import java.util.List;

/**
 * 市场入场出站端口 — 查询指数估值快照。
 */
public interface MarketEntryRepository {

    /**
     * 获取指定指数代码的最新估值快照（含10年PE/PB分位点）。
     */
    MarketIndexSnapshot findLatestByIndexCode(String indexCode);

    /**
     * 批量获取最新估值快照。
     */
    List<MarketIndexSnapshot> findLatestByIndexCodes(List<String> indexCodes);
}