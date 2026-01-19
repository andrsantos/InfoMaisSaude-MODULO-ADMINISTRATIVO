package com.Projeto.InfoMaisSaude;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class InfoMaisSaudeApplication {

	public static void main(String[] args) {
		SpringApplication.run(InfoMaisSaudeApplication.class, args);
	}

	@Bean
	public CommandLineRunner gerarSenha() {
		return args -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			System.out.println("HASH DA SENHA 123456: " + encoder.encode("123456"));
		};
	}

}
