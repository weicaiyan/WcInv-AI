package com.wcinv.application.port.outbound;

import com.wcinv.application.dto.CheapPortfolioResponse;

import java.util.List;

public interface CheapPortfolioRepository {
    List<CheapPortfolioResponse.Stock> findLatestSelectedStocks();
    int countLatestCandidates();
    String findLatestTradeDate();
}
