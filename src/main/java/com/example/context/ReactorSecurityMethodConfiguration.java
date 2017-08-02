package com.example.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.expression.method.*;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityMetadataSourceAdvisor;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.prepost.PrePostAnnotationSecurityMetadataSource;
import org.springframework.security.authorization.method.PrePostMethodInterceptor;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

/**
 * @author Rob Winch
 * @since 5.0
 */
@EnableReactiveMethodSecurity
public class ReactorSecurityMethodConfiguration {

}
