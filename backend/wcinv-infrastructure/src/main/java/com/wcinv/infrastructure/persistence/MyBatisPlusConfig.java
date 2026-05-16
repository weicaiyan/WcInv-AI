package com.wcinv.infrastructure.persistence;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.wcinv.infrastructure.persistence.mapper")
public class MyBatisPlusConfig {
}
