package com.wcinv.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcinv.application.port.outbound.UserRepository;
import com.wcinv.domain.model.User;
import com.wcinv.infrastructure.persistence.entity.UserEntity;
import com.wcinv.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper mapper;

    public UserRepositoryImpl(UserMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        UserEntity entity = mapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    private User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setPasswordHash(entity.getPasswordHash());
        return user;
    }
}
