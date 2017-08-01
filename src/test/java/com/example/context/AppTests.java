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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.Credentials.basicAuthenticationCredentials;
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
	public void postAuthorizeFindMessageByIdWhenMessageToJoeAndJoeRequestsThenFound() {
		client.get()
				.uri("/postAuthorize/messages/1")
				.attributes(basicAuthenticationCredentials("joe", "joe"))
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).consumeWith( response -> assertThat(response.getResponseBody()).contains("Joe"));
	}

	@Test
	public void postAuthorizeFindMessageByIdWhenMessageToRobAndJoeRequestsThenForbidden() {
		client.get()
				.uri("/postAuthorize/messages/20")
				.attributes(basicAuthenticationCredentials("joe", "joe"))
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
				.expectBody().isEmpty();
	}
}
