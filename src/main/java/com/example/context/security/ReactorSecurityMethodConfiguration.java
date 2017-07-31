package com.example.context.security;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.reactivestreams.Publisher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.expression.method.*;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Configuration
@EnableAspectJAutoProxy
public class ReactorSecurityMethodConfiguration {

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public ReactiveAdvisor methodSecurityInterceptor() throws Exception {
		ExpressionBasedAnnotationAttributeFactory attributeFactory = new ExpressionBasedAnnotationAttributeFactory(
				new DefaultMethodSecurityExpressionHandler());
		PrePostAnnotationSecurityMetadataSource source = new PrePostAnnotationSecurityMetadataSource(attributeFactory);
		return new ReactiveAdvisor(source);
	}

	static class ReactiveAdvisor extends AbstractPointcutAdvisor {

		private transient MethodSecurityMetadataSource attributeSource;

		public ReactiveAdvisor(MethodSecurityMetadataSource attributeSource) {
			this.attributeSource = attributeSource;
		}

		@Override public Pointcut getPointcut() {
			return new MethodSecurityMetadataSourcePointcut();
		}

		@Override public Advice getAdvice() {
			return new MethodInterceptor() {
				@Override public Object invoke(final MethodInvocation invocation)
						throws Throwable {


					Collection<ConfigAttribute> attributes = attributeSource
							.getAttributes(invocation.getMethod(),
									invocation.getThis().getClass());

					MethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
					ExpressionBasedPostInvocationAdvice advice = new ExpressionBasedPostInvocationAdvice(handler);

					Mono<Object> result = (Mono<Object>) invocation.proceed();
					return result.delaySubscription(Mono.currentContext().flatMap(ctx -> {
								Mono<Authentication> user = ctx.get("USER");
								PostInvocationAttribute attr = findPostInvocationAttribute(attributes);
								return result.map(r -> {
									System.out.println("hi");
									Authentication auth = user.block();
									return advice.after(auth, invocation, attr, r);
//									return user.map( auth -> {
//										System.out.println("Auth");
//										return advice.after(auth, invocation, attr, r);
//									});
								});
							}));
				}
			};
		}

		class MethodSecurityMetadataSourcePointcut extends StaticMethodMatcherPointcut
				implements Serializable {
			@SuppressWarnings("unchecked")
			public boolean matches(Method m, Class targetClass) {
				Collection attributes = attributeSource.getAttributes(m, targetClass);
				return attributes != null && !attributes.isEmpty();
			}
		}


		private PostInvocationAttribute findPostInvocationAttribute(
				Collection<ConfigAttribute> config) {
			for (ConfigAttribute attribute : config) {
				if (attribute instanceof PostInvocationAttribute) {
					return (PostInvocationAttribute) attribute;
				}
			}

			return null;
		}
	}
}
