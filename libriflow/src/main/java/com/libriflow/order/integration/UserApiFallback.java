package com.libriflow.order.integration;

import org.springframework.stereotype.Component;

@Component
public class UserApiFallback implements UserApi {

    @Override
    public boolean checkUserExists(Long userId) {
        System.out.println("🛡️ CIRCUIT BREAKER: user-service fora do ar! Retornando false por segurança.");
        return false;
    }

    @Override
    public UserDetailsDTO getUserDetails(Long userId) {
        System.out.println("🛡️ CIRCUIT BREAKER: user-service fora do ar! Retornando usuário fantasma.");
        return new UserDetailsDTO(userId, "Usuário Indisponível (Sistema em Recuperação)", "indisponivel@user.com");
    }
}