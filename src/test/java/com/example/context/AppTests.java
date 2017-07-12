package com.example.context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTests {
	@Autowired
	ApplicationContext context;
	WebTestClient client;

	@Before
	public void setup() {
		client = WebTestClient.bindToApplicationContext(context)
				.build();
	}

	@Test
	public void getMessageWhenMessageToJoeAndJoeRequestsThenFound() {
		client.get()
				.uri("/messages/1")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).consumeWith( response -> assertThat(response.getResponseBody()).contains("Joe"));
	}

	@Test
	public void getMessageWhenMessageToRobAndJoeRequestsThenForbidden() {
		client.get()
				.uri("/messages/20")
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
				.expectBody().isEmpty();
	}
}
