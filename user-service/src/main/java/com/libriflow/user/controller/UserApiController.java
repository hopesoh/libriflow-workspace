package com.libriflow.user.controller;

import com.libriflow.user.integration.UserIntegrationService;
import com.libriflow.user.integration.api.UserDetailsDTO;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserApiController {

    private final UserIntegrationService userIntegrationService;

    public UserApiController(UserIntegrationService userIntegrationService) {
        this.userIntegrationService = userIntegrationService;
    }

    @GetMapping("/{userId}/exists")
    public boolean checkUserExists(@PathVariable @Positive Long userId) {
        return userIntegrationService.checkUserExists(userId);
    }

    @GetMapping("/{userId}/details")
    public UserDetailsDTO getUserDetails(@PathVariable @Positive Long userId) {
        return userIntegrationService.getUserDetails(userId);
    }
}
