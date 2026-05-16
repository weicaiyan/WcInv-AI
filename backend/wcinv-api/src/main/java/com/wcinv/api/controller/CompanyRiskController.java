package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.CompanyRiskRequest;
import com.wcinv.application.dto.CompanyRiskResponse;
import com.wcinv.application.usecase.EvaluateCompanyRiskUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/company-risk")
public class CompanyRiskController {
    private final EvaluateCompanyRiskUseCase evaluateCompanyRiskUseCase;

    public CompanyRiskController(EvaluateCompanyRiskUseCase evaluateCompanyRiskUseCase) {
        this.evaluateCompanyRiskUseCase = evaluateCompanyRiskUseCase;
    }

    @PostMapping("/evaluate")
    public ApiResponse<CompanyRiskResponse> evaluate(@RequestBody CompanyRiskRequest request) {
        return ApiResponse.success(evaluateCompanyRiskUseCase.execute(request));
    }
}
