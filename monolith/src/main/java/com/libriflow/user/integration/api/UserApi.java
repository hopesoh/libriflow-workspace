package com.libriflow.user.integration.api;

public interface UserApi {
    boolean checkUserExists(Long userId);

    UserDetailsDTO getUserDetails(Long userId);
}
