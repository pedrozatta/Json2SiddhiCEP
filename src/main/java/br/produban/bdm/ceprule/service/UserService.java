package br.produban.bdm.ceprule.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.produban.bdm.ceprule.commons.ExtendableBean;
import br.produban.bdm.ceprule.commons.UserUtil;
import br.produban.bdm.ceprule.ws.soap.UserAdminServiceClient;

/**
 * Created by pedrozatta
 */

@Service
public class UserService {

	final static Logger logger = Logger.getLogger(UserService.class);

	@Autowired
	protected UserAdminServiceClient userAdminServiceClient;

	protected UserUtil userUtil;

	@PostConstruct
	protected void post() {
		userUtil = new UserUtil();
	}

	public boolean isAcepAdmin() {
		String user = userUtil.getAuthenticatedUserName();
		user = user.substring(user.lastIndexOf("/") + 1);
		List<String> list = userAdminServiceClient.getRolesOfUser(user, "admin-acep", 100);
		for (String item : list) {
			if ("admin-acep".equalsIgnoreCase(item)) {
				return true;
			}
		}
		return false;
	}

	public ExtendableBean getAuthenticatedUserInfo() {
		ExtendableBean user = userUtil.getAuthenticatedUserInfo();
		user.add("admin", this.isAcepAdmin());
		return user;
	}

	public String getAuthenticatedUserName() {

		return userUtil.getAuthenticatedUserName();
	}

}
