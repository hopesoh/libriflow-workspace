package com.libriflow;

import com.libriflow.user.entity.User;
import com.libriflow.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
    CommandLineRunner userDataInitializer(UserRepository userRepository) {
		return args -> {
			// O user-service é o dono exclusivo destes dados
			if (userRepository.count() == 0) {
				User u1 = new User();
				u1.setName("João Silva");
				u1.setEmail("joao@example.com");
				u1.setPassword("123456");
				userRepository.save(u1);

				User u2 = new User();
				u2.setName("Maria Souza");
				u2.setEmail("maria@example.com");
				u2.setPassword("senha123");
				userRepository.save(u2);
				System.out.println("Usuários de teste inseridos no Schema 'auth'.");
			}

			System.out.println("=== User-Service (Porta 8081) rodando e conectado! ===");
		};
	}

}
