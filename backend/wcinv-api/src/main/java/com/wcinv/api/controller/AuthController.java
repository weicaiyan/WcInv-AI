package com.wcinv.api.controller;

import com.wcinv.application.dto.ApiResponse;
import com.wcinv.application.dto.LoginRequest;
import com.wcinv.application.dto.LoginResponse;
import com.wcinv.application.usecase.AuthenticateUseCase;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticateUseCase authenticateUseCase;

    public AuthController(AuthenticateUseCase authenticateUseCase) {
        this.authenticateUseCase = authenticateUseCase;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authenticateUseCase.execute(request);
        return ApiResponse.success(response);
    }
}
