package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.GridPlanRequest;
import com.wcinv.application.dto.GridPlanResponse;
import com.wcinv.application.usecase.CalculateGridPlanUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/grid")
public class GridController {
    private final CalculateGridPlanUseCase calculateGridPlanUseCase;

    public GridController(CalculateGridPlanUseCase calculateGridPlanUseCase) {
        this.calculateGridPlanUseCase = calculateGridPlanUseCase;
    }

    @PostMapping("/calculate")
    public ApiResponse<GridPlanResponse> calculate(@RequestBody GridPlanRequest request) {
        return ApiResponse.success(calculateGridPlanUseCase.execute(request));
    }
}
