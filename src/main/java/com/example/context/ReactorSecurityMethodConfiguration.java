package com.example.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedAnnotationAttributeFactory;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource;
import org.springframework.security.authorization.method.PrePostMethodInterceptor;

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
