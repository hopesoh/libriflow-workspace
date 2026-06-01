package com.libriflow.order.test;

import com.libriflow.order.integration.user.UserApi;
import com.libriflow.order.integration.book.BookApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final UserApi userApi;
    private final BookApi bookApi;

    public TestController(UserApi userApi, BookApi bookApi) {
        this.userApi = userApi;
        this.bookApi = bookApi;
    }

    @GetMapping("/teste-integracao/{userId}")
    public ResponseEntity<String> testeComunicacao(@PathVariable Long userId) {
        boolean existe = userApi.checkUserExists(userId);

        if (existe) {
            return ResponseEntity.ok("👤 Sucesso! O order-service foi na rede e encontrou o usuário " + userId);
        } else {
            return ResponseEntity.ok("👤 Sucesso na rede! Mas o microsserviço disse que o usuário não existe.");
        }
    }

    @GetMapping("/teste-integracao-livro/{bookId}")
    public ResponseEntity<String> testeComunicacaoLivro(@PathVariable Long bookId) {
        boolean existe = bookApi.checkBookExists(bookId);

        if (existe) {
            return ResponseEntity.ok("📚 Sucesso! O order-service foi na rede e encontrou o livro " + bookId);
        } else {
            return ResponseEntity.ok("📚 Sucesso na rede! Mas o microsserviço disse que o livro não existe.");
        }
    }
}