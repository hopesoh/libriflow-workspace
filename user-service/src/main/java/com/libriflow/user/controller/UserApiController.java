package com.libriflow.user.controller;

import com.libriflow.user.integration.UserIntegrationService;
import com.libriflow.user.integration.api.UserDetailsDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    private final UserIntegrationService userIntegrationService;

    public UserApiController(UserIntegrationService userIntegrationService) {
        this.userIntegrationService = userIntegrationService;
    }

    @GetMapping("/{userId}/exists")
    public boolean checkUserExists(@PathVariable Long userId) {
        return userIntegrationService.checkUserExists(userId);
    }

    @GetMapping("/{userId}/details")
    public UserDetailsDTO getUserDetails(@PathVariable Long userId) {
        return userIntegrationService.getUserDetails(userId);
    }
}