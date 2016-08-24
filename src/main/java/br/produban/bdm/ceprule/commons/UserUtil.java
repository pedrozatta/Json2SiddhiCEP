package br.produban.bdm.ceprule.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

/**
 * Created by pedrozatta
 */

@Service
public class UserUtil {

	protected SecurityContext getContext() {
		return SecurityContextHolder.getContext();
	}

	protected Authentication getContextAuthentication() {
		Authentication auth = getContext().getAuthentication();
		return auth;
	}

	@SuppressWarnings("unchecked")
	public ExtendableBean getAuthenticatedUserInfo() {
		ExtendableBean user = new ExtendableBean();
		user.add("userName", getAuthenticatedUserName());
		OAuth2Authentication auth = (OAuth2Authentication) getContextAuthentication();
		Map<String, String> details = (Map<String, String>) auth.getUserAuthentication().getDetails();
		for (Entry<String, String> entry : details.entrySet()) {
			user.add(entry.getKey(), entry.getValue());
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	public String getAuthenticatedUserName() {
		OAuth2Authentication auth = (OAuth2Authentication) getContextAuthentication();
		HashMap<String, String> details = (HashMap<String, String>) auth.getUserAuthentication().getDetails();
		String userName = details.get("sub");
		if (userName.contains("@")) {
			userName = userName.split("@")[0];
		}
		return userName;
	}

}
