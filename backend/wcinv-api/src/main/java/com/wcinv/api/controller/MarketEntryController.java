package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.MarketEntryResponse;
import com.wcinv.application.usecase.EvaluateMarketEntryUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/market")
public class MarketEntryController {

    private final EvaluateMarketEntryUseCase evaluateMarketEntryUseCase;

    public MarketEntryController(EvaluateMarketEntryUseCase evaluateMarketEntryUseCase) {
        this.evaluateMarketEntryUseCase = evaluateMarketEntryUseCase;
    }

    @GetMapping("/entry")
    public ApiResponse<MarketEntryResponse> getEntryDecision() {
        MarketEntryResponse response = evaluateMarketEntryUseCase.execute();
        return ApiResponse.success(response);
    }
}