package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.TemperatureHistoryResponse;
import com.wcinv.application.usecase.TemperatureHistoryUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/indices")
public class TemperatureHistoryController {

    private final TemperatureHistoryUseCase useCase;

    public TemperatureHistoryController(TemperatureHistoryUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/{indexCode}/temperature-history")
    public ApiResponse<TemperatureHistoryResponse> getHistory(
            @PathVariable String indexCode,
            @RequestParam(defaultValue = "3650") int days) {
        return ApiResponse.success(useCase.execute(indexCode, days));
    }
}
