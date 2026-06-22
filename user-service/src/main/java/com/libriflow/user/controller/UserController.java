package com.libriflow.user.controller;

import com.libriflow.user.controller.request.UserCreateRequest;
import com.libriflow.user.controller.request.UserUpdateRequest;
import com.libriflow.user.entity.User;
import com.libriflow.user.integration.api.UserDetailsDTO;
import com.libriflow.user.exception.UserNotFoundException;
import com.libriflow.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDetailsDTO> findAll() {
        return userService
                .findAll()
                .stream()
                .map(UserController::toDetailsDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDTO> findById(@PathVariable @Positive Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(toDetailsDto(user)))
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<UserDetailsDTO> save(@Valid @RequestBody UserCreateRequest request) {
        User savedUser = userService.save(request);
        return ResponseEntity.status(201).body(toDetailsDto(savedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDetailsDTO> update(@PathVariable @Positive Long id, @Valid @RequestBody UserUpdateRequest request) {
        User updatedUser = userService.update(id, request);
        return ResponseEntity.ok(toDetailsDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private static UserDetailsDTO toDetailsDto(User user) {
        return new UserDetailsDTO(user.getId(), user.getName(), user.getEmail());
    }
}
