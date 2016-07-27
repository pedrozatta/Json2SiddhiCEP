package br.produban.bdm.ceprule.service;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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

	@SuppressWarnings("unchecked")
	public String getAuthenticatedUserName() {
		OAuth2Authentication auth = (OAuth2Authentication) getContext().getAuthentication();
		HashMap<String, String> details = (HashMap<String, String>) auth.getUserAuthentication().getDetails();
		String userName = details.get("sub");
		if (userName.contains("@")) {
			userName = userName.split("@")[0];
		}
		return userName;
	}

}
