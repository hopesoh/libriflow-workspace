package com.libriflow.order.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserApi {

    @GetMapping("/api/users/{userId}/exists")
    boolean checkUserExists(@PathVariable("userId") Long userId);

    @GetMapping("/api/users/{userId}/details")
    UserDetailsDTO getUserDetails(@PathVariable Long userId);
}