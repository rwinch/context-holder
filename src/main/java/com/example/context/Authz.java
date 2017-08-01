package com.example.context;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * @author Rob Winch
 * @since 5.0
 */
@Component
public class Authz {
	public boolean check(long id) {
		return id % 2 == 0;
	}

	public boolean check(Authentication authentication, Message message) {
		return message != null &&
				authentication != null &&
				authentication.getName().equals(message.getTo());
	}
}
