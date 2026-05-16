package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.CheapPortfolioResponse;
import com.wcinv.application.usecase.GetCheapPortfolioUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cheap-portfolio")
public class CheapPortfolioController {
    private final GetCheapPortfolioUseCase getCheapPortfolioUseCase;

    public CheapPortfolioController(GetCheapPortfolioUseCase getCheapPortfolioUseCase) {
        this.getCheapPortfolioUseCase = getCheapPortfolioUseCase;
    }

    @GetMapping
    public ApiResponse<CheapPortfolioResponse> getCheapPortfolio() {
        return ApiResponse.success(getCheapPortfolioUseCase.execute());
    }
}
