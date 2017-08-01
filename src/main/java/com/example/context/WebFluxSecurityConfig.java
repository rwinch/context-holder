package com.example.context;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.HttpSecurity;
import org.springframework.security.core.userdetails.MapUserDetailsRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.SecurityReactorContextFilter;

/**
 * @author Rob Winch
 * @since 5.0
 */
@EnableWebFluxSecurity
public class WebFluxSecurityConfig {

	@Bean
	public SecurityWebFilterChain springSecurityFilter(HttpSecurity http) {
		return http
				.build();
	}

	@Bean
	public SecurityReactorContextFilter securityReactorContextFilter() {
		return new SecurityReactorContextFilter();
	}

	@Bean
	public MapUserDetailsRepository userDetailsRepository() {
		UserDetails rob = User.withUsername("rob").password("rob").roles("USER").build();
		UserDetails joe = User.withUsername("joe").password("joe").roles("USER").build();
		UserDetails admin = User.withUsername("admin").password("admin").roles("USER","ADMIN").build();
		return new MapUserDetailsRepository(rob, joe, admin);
	}
}
