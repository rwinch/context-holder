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

import java.util.Map;
import java.util.function.Consumer;

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
				.attributes(basicAuthenticationCredentials("rob", "rob"))
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).consumeWith( response -> assertThat(response.getResponseBody()).contains("Joe"));
	}

	@Test
	public void getMessageWhenMessageToRobAndJoeRequestsThenForbidden() {
		client.get()
				.uri("/messages/20")
				.attributes(new BasicAuthenticationCredential("joe", "joe"))
				.exchange()
				.expectStatus().isEqualTo(HttpStatus.FORBIDDEN)
				.expectBody().isEmpty();
	}

	static BasicAuthenticationCredential basicAuthenticationCredentials(String username, String password) {
		return new BasicAuthenticationCredential(username, password);
	}

	static class BasicAuthenticationCredential implements Consumer<Map<String,Object>> {
		private static String ATTRIBUTE_NAME = BasicAuthenticationCredential.class.getName().concat(".ATTRIBUTE_NAME");

		private final String username;
		private final String password;


		BasicAuthenticationCredential(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		@Override
		public void accept(Map<String, Object> attributes) {
			attributes.put(ATTRIBUTE_NAME, this);
		}

		public static BasicAuthenticationCredential get(Map<String,Object> attributes) {
			return (BasicAuthenticationCredential) attributes.get(ATTRIBUTE_NAME);
		}
	}
}
