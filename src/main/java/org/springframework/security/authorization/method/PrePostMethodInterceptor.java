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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedPostInvocationAdvice;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PostInvocationAuthorizationAdvice;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author Rob Winch
 * @since 5.0
 */
public class PrePostMethodInterceptor implements MethodInterceptor {
	private final MethodSecurityMetadataSource attributeSource;

	private PostInvocationAuthorizationAdvice postAdvice;

	private PreInvocationAuthorizationAdvice preAdvice;

	public PrePostMethodInterceptor(MethodSecurityMetadataSource attributeSource) {
		this.attributeSource = attributeSource;

		MethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		this.postAdvice = new ExpressionBasedPostInvocationAdvice(handler);
		this.preAdvice = new ExpressionBasedPreInvocationAdvice();
	}

	public void setPostAdvice(PostInvocationAuthorizationAdvice postAdvice) {
		Assert.notNull(postAdvice, "postAdvice cannot be null");
		this.postAdvice = postAdvice;
	}

	public void setPreAdvice(PreInvocationAuthorizationAdvice preAdvice) {
		Assert.notNull(preAdvice, "preAdvice cannot be null");
		this.preAdvice = preAdvice;
	}

	@Override
	public Object invoke(final MethodInvocation invocation)
			throws Throwable {
		Method method = invocation.getMethod();
		Class<?> targetClass = invocation.getThis().getClass();
		Collection<ConfigAttribute> attributes = this.attributeSource
				.getAttributes(method, targetClass);

		PreInvocationAttribute preAttr = findPreInvocationAttribute(attributes);
		return Mono.currentContext()
			.flatMap( cxt -> cxt.getOrDefault(Authentication.class, Mono.<Authentication>empty()))
			.filter( auth -> this.preAdvice.before(auth, invocation, preAttr))
			.switchIfEmpty(Mono.error(new AccessDeniedException("Denied")))
			.flatMap( auth -> invoke(auth, invocation, attributes));
	}

	private Mono<?> invoke(final Authentication auth, final MethodInvocation invocation, Collection<ConfigAttribute> attributes) {
		Mono<?> result = performInvoke(invocation);
		PostInvocationAttribute attr = findPostInvocationAttribute(attributes);
		if(attr == null) {
			return result;
		}
		return result.map( r -> this.postAdvice.after(auth, invocation, attr, r));
	}

	private Mono<?> performInvoke(final MethodInvocation invocation) {
		try {
			return (Mono<?>) invocation.proceed();
		} catch(Throwable throwable) {
			throw Exceptions.propagate(throwable);
		}
	}

	private static PostInvocationAttribute findPostInvocationAttribute(
			Collection<ConfigAttribute> config) {
		for (ConfigAttribute attribute : config) {
			if (attribute instanceof PostInvocationAttribute) {
				return (PostInvocationAttribute) attribute;
			}
		}

		return null;
	}

	private PreInvocationAttribute findPreInvocationAttribute(
			Collection<ConfigAttribute> config) {
		for (ConfigAttribute attribute : config) {
			if (attribute instanceof PreInvocationAttribute) {
				return (PreInvocationAttribute) attribute;
			}
		}

		return null;
	}
}
