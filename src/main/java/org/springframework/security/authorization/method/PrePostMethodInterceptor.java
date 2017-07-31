package org.springframework.security.authorization.method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedPostInvocationAdvice;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.core.Authentication;
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
		ExpressionBasedPostInvocationAdvice advice = new ExpressionBasedPostInvocationAdvice(handler);

		Mono<?> result = (Mono<?>) invocation.proceed();
		return Mono.currentContext().flatMap(ctx -> {
			Mono<Authentication> user = ctx.get("USER");
			PostInvocationAttribute attr = findPostInvocationAttribute(attributes);
			return result.flatMap(r -> {
//				return advice.after(user.block(), invocation, attr, r);
				return user
						.map ( auth -> advice.after(auth, invocation, attr, r));
				//									return user.map( auth -> {
				//										System.out.println("Auth");
				//										return advice.after(auth, invocation, attr, r);
				//									});
			});
		});
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
}
