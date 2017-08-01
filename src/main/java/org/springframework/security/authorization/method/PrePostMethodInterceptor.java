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
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.core.Authentication;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author Rob Winch
 * @since 5.0
 */
public class PrePostMethodInterceptor implements MethodInterceptor {
	private final MethodSecurityMetadataSource attributeSource;

	public PrePostMethodInterceptor(MethodSecurityMetadataSource attributeSource) {
		this.attributeSource = attributeSource;
	}

	@Override
	public Object invoke(final MethodInvocation invocation)
			throws Throwable {
		Collection<ConfigAttribute> attributes = attributeSource
				.getAttributes(invocation.getMethod(),
						invocation.getThis().getClass());

		MethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		ExpressionBasedPostInvocationAdvice postAdvice = new ExpressionBasedPostInvocationAdvice(handler);
		ExpressionBasedPreInvocationAdvice preAdvice = new ExpressionBasedPreInvocationAdvice();
		preAdvice.setExpressionHandler(handler);

		PreInvocationAttribute preAttr = findPreInvocationAttribute(attributes);
		return Mono.currentContext()
			.flatMap( cxt -> cxt.<Mono<Authentication>>get("USER"))
			.filter( auth -> preAdvice.before(auth, invocation, preAttr))
			.switchIfEmpty(Mono.error(new AccessDeniedException("Denied")))
			.flatMap( auth -> invoke(auth, invocation, attributes));
	}

	private Mono<?> invoke(final Authentication auth, final MethodInvocation invocation, Collection<ConfigAttribute> attributes) {
		MethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		ExpressionBasedPostInvocationAdvice postAdvice = new ExpressionBasedPostInvocationAdvice(handler);

		Mono<?> result = performInvoke(invocation);
		PostInvocationAttribute attr = findPostInvocationAttribute(attributes);
		if(attr == null) {
			return result;
		}
		return result.map( r -> postAdvice.after(auth, invocation, attr, r));
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
