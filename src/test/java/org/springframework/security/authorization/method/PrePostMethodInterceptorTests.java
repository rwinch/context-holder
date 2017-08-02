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

import com.example.context.Message;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.AccessDeniedException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

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
	Mono<String> result = Mono.just("message");

	@After
	public void cleanup() {
		reset(delegate);
	}

	@Autowired
	public void setConfig(Config config) {
		this.delegate = config.delegate;
	}

	@Test
	public void run() {
		when(this.delegate.findById(anyInt())).thenReturn(result);

		StepVerifier
				.create(this.messageService.findById(1L))
				.expectError(AccessDeniedException.class)
				.verify();

		StepVerifier.create(result)
				// how to verify no 
				.expectSubscription()
				.verifyComplete();
	}

	@EnableReactiveMethodSecurity(prePostEnabled = true)
	static class Config {
		MessageService delegate = mock(MessageService.class);

		@Bean
		public DelegateMessageService defaultMessageService() {
			return new DelegateMessageService(delegate);
		}
	}
}