package com.example.context;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Rob Winch
 * @since 5.0
 */
@ControllerAdvice
public class SecurityControllerAdvice {
	@ExceptionHandler
	public void handle(AccessDeniedException denied) {
		throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied", denied);
	}
}
