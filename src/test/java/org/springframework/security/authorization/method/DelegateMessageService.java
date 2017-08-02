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

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import reactor.core.publisher.Mono;

public class DelegateMessageService implements MessageService {
	private final MessageService delegate;

	public DelegateMessageService(MessageService delegate) {
		this.delegate = delegate;
	}

	@Override
	public Mono<String> findById(long id) {
		return delegate.findById(id);
	}

	@Override
	@PreAuthorize("hasRole('ADMIN')")
	public Mono<String> preAuthorizeHasRoleFindById(
			long id) {
		return delegate.preAuthorizeHasRoleFindById(id);
	}

	@Override
	@PostAuthorize("returnObject.to == authentication?.name")
	public Mono<String> postAuthorizeFindById(
			long id) {
		return delegate.postAuthorizeFindById(id);
	}

	@Override
	@PreAuthorize("@authz.check(#id)")
	public Mono<String> preAuthorizeBeanFindById(
			long id) {
		return delegate.preAuthorizeBeanFindById(id);
	}

	@Override
	@PostAuthorize("@authz.check(authentication, returnObject)")
	public Mono<String> postAuthorizeBeanFindById(
			long id) {
		return delegate.postAuthorizeBeanFindById(id);
	}

}
