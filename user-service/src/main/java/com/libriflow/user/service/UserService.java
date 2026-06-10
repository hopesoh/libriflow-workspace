package com.libriflow.user.service;

import com.libriflow.user.controller.request.UserCreateRequest;
import com.libriflow.user.controller.request.UserUpdateRequest;
import com.libriflow.user.entity.User;
import com.libriflow.user.exception.EmailAlreadyInUseException;
import com.libriflow.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(UserCreateRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        ensureEmailAvailable(normalizedEmail, null);

        User user = new User();
        user.setName(normalizeName(request.name()));
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }

    public User update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));

        String normalizedEmail = normalizeEmail(request.email());
        ensureEmailAvailable(normalizedEmail, id);

        user.setName(normalizeName(request.name()));
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));
        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsBy(Long id) {
        return userRepository.findById(id).isPresent();
    }

    private void ensureEmailAvailable(String email, Long userId) {
        userRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new EmailAlreadyInUseException(existing.getEmail());
                });
    }

    private String normalizeName(String name) {
        return name.trim().replaceAll("\\s+", " ");
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
