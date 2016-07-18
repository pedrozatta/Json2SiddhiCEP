package br.produban.services;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Created by pedrozatta
 */

@Service
public class UserService {

	final static Logger logger = Logger.getLogger(UserService.class);

	protected SecurityContext getContext() {
		return SecurityContextHolder.getContext();
	}

	public Authentication getContextAuthentication() {
		Authentication auth = getContext().getAuthentication();
		return auth;
	}

	public String getAuthenticatedUserName() {
		Authentication auth = getContext().getAuthentication();
		return auth.getName();
	}

}
