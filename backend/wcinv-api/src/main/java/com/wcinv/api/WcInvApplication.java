package com.wcinv.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.wcinv")
public class WcInvApplication {

    public static void main(String[] args) {
        SpringApplication.run(WcInvApplication.class, args);
    }
}
