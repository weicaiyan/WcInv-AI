package com.wcinv.application.port.outbound;

import com.wcinv.domain.model.IndexValuation;

import java.util.List;
import java.util.Optional;

/**
 * 指数估值出站端口（持久化）。
 */
public interface IndexValuationRepository {

    List<IndexValuation> findByIndexCode(String indexCode);

    Optional<IndexValuation> findByIndexCodeAndTradeDate(String indexCode, String tradeDate);

    void save(IndexValuation valuation);

    List<IndexValuation> findLatestByIndexCodes(List<String> indexCodes);
}
