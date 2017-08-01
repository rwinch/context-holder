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

import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

	private final MessageService messageService;

	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@GetMapping("/postAuthorize/messages/{id}")
	Mono<Message> postAuthorizeFindById(@PathVariable Long id) {
		return this.messageService.postAuthorizeFindById(id);
	}

	@GetMapping("/preAuthorize/hasRole/messages/{id}")
	Mono<Message>preAuthorizeHasRoleFindById(@PathVariable Long id) {
		return this.messageService.preAuthorizeHasRoleFindById(id);
	}

	@GetMapping("/preAuthorize/bean/messages/{id}")
	Mono<Message>preAuthorizeBeanFindById(@PathVariable Long id) {
		return this.messageService.preAuthorizeBeanFindById(id);
	}

	@GetMapping("/postAuthorize/bean/messages/{id}")
	Mono<Message>postAuthorizeBeanFindById(@PathVariable Long id) {
		return this.messageService.postAuthorizeBeanFindById(id);
	}

	@GetMapping("/messages/{id}")
	Mono<Message> findById(@PathVariable Long id) {
		return this.messageService.findById(id);
	}

}
