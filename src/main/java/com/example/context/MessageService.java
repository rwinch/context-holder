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

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Mono;

public interface MessageService {

	Mono<Message> findById(long id);

	@PreAuthorize("hasRole('ADMIN')")
	Mono<Message> preAuthorizeHasRoleFindById(long id);

	@PostAuthorize("returnObject.to == authentication?.name")
	Mono<Message> postAuthorizeFindById(long id);

	@PreAuthorize("@authz.check(#id)")
	Mono<Message> preAuthorizeBeanFindById(long id);

	@PostAuthorize("@authz.check(authentication, returnObject)")
	Mono<Message> postAuthorizeBeanFindById(long id);
}
