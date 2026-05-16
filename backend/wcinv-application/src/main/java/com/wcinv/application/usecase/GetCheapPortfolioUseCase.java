package com.wcinv.application.usecase;

import com.wcinv.application.dto.CheapPortfolioResponse;
import com.wcinv.application.dto.MarketEntryResponse;
import com.wcinv.application.port.outbound.CheapPortfolioRepository;

import java.util.List;

public class GetCheapPortfolioUseCase {
    private final CheapPortfolioRepository cheapPortfolioRepository;
    private final EvaluateMarketEntryUseCase evaluateMarketEntryUseCase;

    public GetCheapPortfolioUseCase(CheapPortfolioRepository cheapPortfolioRepository,
                                    EvaluateMarketEntryUseCase evaluateMarketEntryUseCase) {
        this.cheapPortfolioRepository = cheapPortfolioRepository;
        this.evaluateMarketEntryUseCase = evaluateMarketEntryUseCase;
    }

    public CheapPortfolioResponse execute() {
        MarketEntryResponse entry = evaluateMarketEntryUseCase.execute();
        List<CheapPortfolioResponse.Stock> stocks = cheapPortfolioRepository.findLatestSelectedStocks();

        CheapPortfolioResponse response = new CheapPortfolioResponse();
        response.setTradeDate(cheapPortfolioRepository.findLatestTradeDate());
        response.setEntryAllowed(entry.isAllowNext());
        response.setEntryMessage(entry.isAllowNext()
                ? "市场入场条件满足，可以执行便宜组合筛选。"
                : "市场入场条件不满足，当前组合仅供观察，不建议买入。");
        response.setCandidateCount(cheapPortfolioRepository.countLatestCandidates());
        response.setSelectedCount(stocks.size());
        response.setStocks(stocks);
        return response;
    }
}
