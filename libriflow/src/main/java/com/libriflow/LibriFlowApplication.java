package com.libriflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LibriFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibriFlowApplication.class, args);
    }
}