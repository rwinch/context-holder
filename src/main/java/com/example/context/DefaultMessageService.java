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

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultMessageService implements MessageService {
	private final Map<Long, Message> messages = new ConcurrentHashMap<>();

	public DefaultMessageService() {
		messages.put(1L, new Message("joe","rob", "This is from Joe to Rob"));
		messages.put(20L, new Message("rob","joe", "This is from Rob to Joe"));
	}

	@Override
	public Mono<Message> findById(long id) {
		return remoteFindById(id);
	}

	private Mono<Message> remoteFindById(long id) {
		return Mono.delay(Duration.ofSeconds(1)).map(aLong -> messages.get(id));
	}

}
