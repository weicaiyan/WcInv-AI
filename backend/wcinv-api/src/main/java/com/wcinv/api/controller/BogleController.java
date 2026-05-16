package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.BogleResponse;
import com.wcinv.application.usecase.EvaluateBogleUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bogle")
public class BogleController {

    private final EvaluateBogleUseCase evaluateBogleUseCase;

    public BogleController(EvaluateBogleUseCase evaluateBogleUseCase) {
        this.evaluateBogleUseCase = evaluateBogleUseCase;
    }

    @GetMapping
    public ApiResponse<BogleResponse> getBogleDecision() {
        return ApiResponse.success(evaluateBogleUseCase.execute());
    }
}
