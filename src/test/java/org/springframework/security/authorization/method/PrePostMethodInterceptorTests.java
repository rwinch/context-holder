/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.authorization.method;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;
import reactor.util.context.Context;

import java.util.function.Function;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class PrePostMethodInterceptorTests {
	@Autowired
	MessageService messageService;
	MessageService delegate;
	TestPublisher<String> result = TestPublisher.create();

	Function<Context, Context> withAdmin = context -> context.put(Authentication.class, Mono.just(new TestingAuthenticationToken("admin","password","ROLE_USER", "ROLE_ADMIN")));
	Function<Context, Context> withUser = context -> context.put(Authentication.class, Mono.just(new TestingAuthenticationToken("user","password","ROLE_USER")));

	@After
	public void cleanup() {
		reset(delegate);
	}

	@Autowired
	public void setConfig(Config config) {
		this.delegate = config.delegate;
	}

	@Test
	public void whenPermitAllThenAopDoesNotSubscribe() {
		when(this.delegate.findById(1L)).thenReturn(Mono.from(result));

		this.delegate.findById(1L);

		result.assertNoSubscribers();
	}

	@Test
	public void whenPermitAllThenSuccess() {
		when(this.delegate.findById(1L)).thenReturn(Mono.just("success"));

		StepVerifier.create(this.delegate.findById(1L))
			.expectNext("success")
			.verifyComplete();
	}

	@Test
	public void preAuthorizeHasRoleWhenGrantedThenSuccess() {
		when(this.delegate.preAuthorizeHasRoleFindById(1L)).thenReturn(Mono.just("result"));

		Mono<String> findById = this.messageService.preAuthorizeHasRoleFindById(1L)
				.contextStart(withAdmin);
		assertThat(findById.block()).isEqualTo("result");
		StepVerifier
				.create(findById)
				.expectNext("result")
				.verifyComplete();
	}

	@Test
	public void preAuthorizeHasRoleWhenNoAuthenticationThenDenied() {
		when(this.delegate.preAuthorizeHasRoleFindById(1L)).thenReturn(Mono.from(result));

		Mono<String> findById = this.messageService.preAuthorizeHasRoleFindById(1L);
		StepVerifier
				.create(findById)
				.expectError(AccessDeniedException.class)
				.verify();

		result.assertNoSubscribers();
	}

	@Test
	public void preAuthorizeHasRoleWhenNotAuthorizedThenDenied() {
		when(this.delegate.preAuthorizeHasRoleFindById(1L)).thenReturn(Mono.from(result));

		Mono<String> findById = this.messageService.preAuthorizeHasRoleFindById(1L)
				.contextStart(withUser);
		StepVerifier
				.create(findById)
				.expectError(AccessDeniedException.class)
				.verify();

		result.assertNoSubscribers();
	}

	@Test
	public void preAuthorizeBeanWhenGrantedThenSuccess() {
		when(this.delegate.preAuthorizeBeanFindById(2L)).thenReturn(Mono.just("result"));

		Mono<String> findById = this.messageService.preAuthorizeBeanFindById(2L)
				.contextStart(withAdmin);
		assertThat(findById.block()).isEqualTo("result");
		StepVerifier
				.create(findById)
				.expectNext("result")
				.verifyComplete();
	}

	@Test
	public void preAuthorizeBeanWhenNotAuthenticatedAndGrantedThenSuccess() {
		when(this.delegate.preAuthorizeBeanFindById(2L)).thenReturn(Mono.just("result"));

		Mono<String> findById = this.messageService.preAuthorizeBeanFindById(2L);
		assertThat(findById.block()).isEqualTo("result");
		StepVerifier
				.create(findById)
				.expectNext("result")
				.verifyComplete();
	}

	@Test
	public void preAuthorizeBeanWhenNoAuthenticationThenDenied() {
		when(this.delegate.preAuthorizeBeanFindById(1L)).thenReturn(Mono.from(result));

		Mono<String> findById = this.messageService.preAuthorizeBeanFindById(1L);
		StepVerifier
				.create(findById)
				.expectError(AccessDeniedException.class)
				.verify();

		result.assertNoSubscribers();
	}

	@Test
	public void preAuthorizeBeanWhenNotAuthorizedThenDenied() {
		when(this.delegate.preAuthorizeBeanFindById(1L)).thenReturn(Mono.from(result));

		Mono<String> findById = this.messageService.preAuthorizeBeanFindById(1L)
				.contextStart(withUser);
		StepVerifier
				.create(findById)
				.expectError(AccessDeniedException.class)
				.verify();

		result.assertNoSubscribers();
	}

	@Test
	public void postAuthorizeWhenAuthorizedThenSuccess() {
		when(this.delegate.postAuthorizeFindById(1L)).thenReturn(Mono.just("user"));

		Mono<String> findById = this.messageService.postAuthorizeFindById(1L)
				.contextStart(withUser);
		StepVerifier
				.create(findById)
				.expectNext("user")
				.verifyComplete();
	}

	@Test
	public void postAuthorizeWhenNotAuthorizedThenDenied() {
		when(this.delegate.postAuthorizeBeanFindById(1L)).thenReturn(Mono.just("not-authorized"));

		Mono<String> findById = this.messageService.postAuthorizeBeanFindById(1L)
				.contextStart(withUser);
		StepVerifier
				.create(findById)
				.expectError(AccessDeniedException.class)
				.verify();
	}

	@Test
	public void postAuthorizeWhenBeanAndAuthorizedThenSuccess() {
		when(this.delegate.postAuthorizeBeanFindById(2L)).thenReturn(Mono.just("user"));

		Mono<String> findById = this.messageService.postAuthorizeBeanFindById(2L)
				.contextStart(withUser);
		StepVerifier
				.create(findById)
				.expectNext("user")
				.verifyComplete();
	}

	@Test
	public void postAuthorizeWhenBeanAndNotAuthenticatedAndAuthorizedThenSuccess() {
		when(this.delegate.postAuthorizeBeanFindById(2L)).thenReturn(Mono.just("anonymous"));

		Mono<String> findById = this.messageService.postAuthorizeBeanFindById(2L);
		StepVerifier
				.create(findById)
				.expectNext("anonymous")
				.verifyComplete();
	}

	@Test
	public void postAuthorizeWhenBeanAndNotAuthorizedThenDenied() {
		when(this.delegate.postAuthorizeBeanFindById(1L)).thenReturn(Mono.just("not-authorized"));

		Mono<String> findById = this.messageService.postAuthorizeBeanFindById(1L)
				.contextStart(withUser);
		StepVerifier
				.create(findById)
				.expectError(AccessDeniedException.class)
				.verify();
	}

	@EnableReactiveMethodSecurity(prePostEnabled = true)
	static class Config {
		MessageService delegate = mock(MessageService.class);

		@Bean
		public DelegateMessageService defaultMessageService() {
			return new DelegateMessageService(delegate);
		}

		@Bean
		public Authz authz() {
			return new Authz();
		}
	}
}