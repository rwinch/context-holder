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

package org.springframework.security.config.annotation.method.configuration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.expression.method.*;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource;
import org.springframework.security.authorization.method.PrePostMethodInterceptor;

/**
 * @author Rob Winch
 * @since 5.0
 */
@EnableAspectJAutoProxy
public class ReactiveMethodSecurityConfiguration {

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
	public PrePostMethodInterceptor securityMethodInterceptor(AbstractMethodSecurityMetadataSource source, MethodSecurityExpressionHandler handler) {

		ExpressionBasedPostInvocationAdvice postAdvice = new ExpressionBasedPostInvocationAdvice(
				handler);
		ExpressionBasedPreInvocationAdvice preAdvice = new ExpressionBasedPreInvocationAdvice();
		preAdvice.setExpressionHandler(handler);

		PrePostMethodInterceptor result = new PrePostMethodInterceptor(source);
		result.setPostAdvice(postAdvice);
		result.setPreAdvice(preAdvice);
		return result;
	}

	@Bean
	public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler() {
		return new DefaultMethodSecurityExpressionHandler();
	}
}
