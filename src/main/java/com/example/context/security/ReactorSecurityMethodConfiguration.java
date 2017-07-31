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
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PostInvocationAttribute;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authorization.method.PrePostMethodInterceptor;
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
	public MethodSecurityMetadataSourceAdvisor methodSecurityInterceptor(AbstractMethodSecurityMetadataSource source) throws Exception {
		return new MethodSecurityMetadataSourceAdvisor("securityMethodInterceptor", source, "methodMetadataSource");
	}

	@Bean
	public PrePostAnnotationSecurityMetadataSource methodMetadataSource() {
		ExpressionBasedAnnotationAttributeFactory attributeFactory = new ExpressionBasedAnnotationAttributeFactory(
				new DefaultMethodSecurityExpressionHandler());
		return new PrePostAnnotationSecurityMetadataSource(attributeFactory);
	}

	@Bean
	public PrePostMethodInterceptor securityMethodInterceptor(AbstractMethodSecurityMetadataSource source) {
		return new PrePostMethodInterceptor(source);
	}
}
