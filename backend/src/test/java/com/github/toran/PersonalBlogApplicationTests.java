package com.github.toran;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class PersonalBlogApplicationTests {

	@Test
	void contextLoads() {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				"admin", "admin123"
		);

		System.out.println(authenticationToken);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Test
	void testPasswordEncoding() {
		String rawPassword = "admin";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		System.out.println("原始密码: " + rawPassword);
		System.out.println("加密后密码: " + encodedPassword);
	}


}
