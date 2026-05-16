package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.BondBalanceRequest;
import com.wcinv.application.dto.BondBalanceResponse;
import com.wcinv.application.usecase.CalculateBondBalanceUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bond-balance")
public class BondBalanceController {
    private final CalculateBondBalanceUseCase calculateBondBalanceUseCase;

    public BondBalanceController(CalculateBondBalanceUseCase calculateBondBalanceUseCase) {
        this.calculateBondBalanceUseCase = calculateBondBalanceUseCase;
    }

    @PostMapping("/calculate")
    public ApiResponse<BondBalanceResponse> calculate(@RequestBody BondBalanceRequest request) {
        return ApiResponse.success(calculateBondBalanceUseCase.execute(request));
    }
}
