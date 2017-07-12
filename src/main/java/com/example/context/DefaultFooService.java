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
package com.example.context;

import java.time.Duration;

import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

@Service
public class DefaultFooService implements FooService {

	@Override
	public Mono<String> getFoo(long id) {
		return Mono.currentContext()
					.flatMap(ctx -> invokeRemoteService(id, ctx.get(ServiceInfo.class)));
	}

	private Mono<String> invokeRemoteService(long id, ServiceInfo info) {
		return Mono.delay(Duration.ofSeconds(1)).map(aLong ->
				"request::" + info.getTraceId() +
				", user::" + info.getUser() +
				", agent::" + info.getHeaders().getFirst("User-Agent") +
				", id::" + id);
	}

}
