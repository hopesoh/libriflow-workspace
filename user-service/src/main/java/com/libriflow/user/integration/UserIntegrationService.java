package com.libriflow.user.integration;

import com.libriflow.user.repository.UserRepository;
import com.libriflow.user.integration.api.UserApi;
import com.libriflow.user.integration.api.UserDetailsDTO;
import com.libriflow.user.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserIntegrationService implements UserApi {

    private final UserRepository userRepository;

    public UserIntegrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean checkUserExists(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public UserDetailsDTO getUserDetails(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new UserDetailsDTO(user.getId(), user.getName(), user.getEmail()))
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
