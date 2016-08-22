package br.produban.bdm.ceprule.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import br.produban.bdm.ceprule.commons.ExtendableBean;
import br.produban.bdm.ceprule.ws.soap.UserAdminServiceClient;

/**
 * Created by pedrozatta
 */

@Service
public class UserService {

	final static Logger logger = Logger.getLogger(UserService.class);

	@Autowired
	protected UserAdminServiceClient userAdminServiceClient;

	protected SecurityContext getContext() {
		return SecurityContextHolder.getContext();
	}

	public boolean isAcepAdmin() {
		String user = getAuthenticatedUserName();
		user = user.substring(user.lastIndexOf("/") + 1);
		List<String> list = userAdminServiceClient.getRolesOfUser(user, "admin-acep", 100);
		for (String item : list) {
			if ("admin-acep".equalsIgnoreCase(item)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public ExtendableBean getAuthenticatedUserInfo() {
		ExtendableBean user = new ExtendableBean();
		user.add("admin", this.isAcepAdmin());
		user.add("userName", this.getAuthenticatedUserName());
		OAuth2Authentication auth = (OAuth2Authentication) getContext().getAuthentication();
		Map<String, String> details = (Map<String, String>) auth.getUserAuthentication().getDetails();
		for (Entry<String, String> entry : details.entrySet()) {
			user.add(entry.getKey(), entry.getValue());
		}
		return user;
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
