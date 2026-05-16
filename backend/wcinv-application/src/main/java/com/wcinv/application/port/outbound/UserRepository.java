package com.wcinv.application.port.outbound;

import com.wcinv.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);
}
