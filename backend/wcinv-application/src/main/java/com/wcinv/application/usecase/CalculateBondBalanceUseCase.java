package com.wcinv.application.usecase;

import com.wcinv.application.dto.BondBalanceRequest;
import com.wcinv.application.dto.BondBalanceResponse;
import com.wcinv.domain.model.BondBalancePlan;
import com.wcinv.domain.service.BondBalanceCalculator;

public class CalculateBondBalanceUseCase {
    private final BondBalanceCalculator calculator;

    public CalculateBondBalanceUseCase(BondBalanceCalculator calculator) {
        this.calculator = calculator;
    }

    public BondBalanceResponse execute(BondBalanceRequest request) {
        if (request.getTemperature() == null) {
            throw new IllegalArgumentException("请提供中证全指温度（PE分位点 + PB分位点）/ 2");
        }
        BondBalancePlan plan = calculator.calculate(request.getTemperature());
        return BondBalanceResponse.from(plan);
    }
}
