package com.libriflow.order;

import com.libriflow.order.integration.UserApi;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TestController {

    private final UserApi userApi;

    public TestController(UserApi userApi) {
        this.userApi = userApi;
    }

    @GetMapping("/teste-integracao/{userId}")
    public ResponseEntity<String> testeComunicacao(@PathVariable Long userId) {
        boolean existe = userApi.checkUserExists(userId);

        if (existe) {
            return ResponseEntity.ok("Sucesso! O Monólito foi na rede e encontrou o usuário " + userId);
        } else {
            return ResponseEntity.ok("Sucesso na rede! Mas o microsserviço disse que o usuário não existe.");
        }
    }
}
