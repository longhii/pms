package com.longhi.pms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class PmsApplication {

	public static void main(String[] args) {
		String senhaCriptografado = new BCryptPasswordEncoder().encode("123456");
		System.out.println(senhaCriptografado);


		SpringApplication.run(PmsApplication.class, args);
	}

}
