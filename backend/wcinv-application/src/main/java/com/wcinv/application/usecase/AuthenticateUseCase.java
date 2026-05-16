package com.wcinv.application.usecase;

import com.wcinv.application.dto.LoginRequest;
import com.wcinv.application.dto.LoginResponse;
import com.wcinv.application.exception.BizErrorCode;
import com.wcinv.application.port.outbound.TokenService;
import com.wcinv.application.port.outbound.UserRepository;
import com.wcinv.domain.model.User;
import com.wcinv.shared.exception.BizException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class AuthenticateUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthenticateUseCase(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public LoginResponse execute(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (!userOpt.isPresent()) {
            throw new BizException(BizErrorCode.AUTH_FAILED);
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BizException(BizErrorCode.AUTH_FAILED);
        }
        String token = tokenService.generateToken(user.getUsername());
        return new LoginResponse(token, 604800);
    }
}
