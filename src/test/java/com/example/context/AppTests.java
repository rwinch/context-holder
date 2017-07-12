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
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTests {
	@Autowired
	ApplicationContext context;
	WebTestClient client;

	@Before
	public void setup() {
		client = WebTestClient
				.bindToApplicationContext(context)
				.configureClient()
				.filter(basicAuthentication())
				.build();
	}

	@Test
	public void getMessageWhenMessageToJoeAndJoeRequestsThenFound() {
		client.get()
				.uri("/messages/1")
				.attribute(ExchangeFilterFunctions.USERNAME_ATTRIBUTE, "rob")
				.attribute(ExchangeFilterFunctions.PASSWORD_ATTRIBUTE, "rob")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).consumeWith( response -> assertThat(response.getResponseBody()).contains("Joe"));
	}

	@Test
	public void getMessageWhenMessageToRobAndJoeRequestsThenForbidden() {
		client.get()
				.uri("/messages/20")
				.attribute(ExchangeFilterFunctions.USERNAME_ATTRIBUTE, "joe")
				.attribute(ExchangeFilterFunctions.PASSWORD_ATTRIBUTE, "joe")
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
				.expectBody().isEmpty();
	}
}
