package com.example.context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTests {

	@Test
	public void contextLoads() {

		WebClient.create()
		         .get()
		         .uri("http://localhost:8080/foo/1")
		         .retrieve()
		         .bodyToMono(String.class)
		         .block();
	}

}
