package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.IndexTemperatureResponse;
import com.wcinv.application.usecase.CalculateTemperatureUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/indices")
public class TemperatureController {

    private final CalculateTemperatureUseCase calculateTemperatureUseCase;

    public TemperatureController(CalculateTemperatureUseCase calculateTemperatureUseCase) {
        this.calculateTemperatureUseCase = calculateTemperatureUseCase;
    }

    @GetMapping("/temperatures")
    public ApiResponse<List<IndexTemperatureResponse>> getTemperatures() {
        List<IndexTemperatureResponse> temperatures = calculateTemperatureUseCase.execute();
        return ApiResponse.success(temperatures);
    }
}
