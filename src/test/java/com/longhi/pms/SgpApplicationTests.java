package com.longhi.pms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class PmsApplicationTests {

	@Test
	void criarHashSenha() {
		var hash = new BCryptPasswordEncoder().encode("123456");
		System.out.println(hash);
	}

}
