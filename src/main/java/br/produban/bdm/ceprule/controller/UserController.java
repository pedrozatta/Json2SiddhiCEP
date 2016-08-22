package br.produban.bdm.ceprule.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.produban.bdm.ceprule.service.UserService;
import br.produban.bdm.commons.ExtendableBean;

/**
 * Created by pedrozatta
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/user")
public class UserController {

	final static Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	protected UserService userService;

	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

	@RequestMapping(value = "/info", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ExtendableBean info() {
		ExtendableBean extendableBean = new ExtendableBean();
		extendableBean.add("acep-admin", userService.isAcepAdmin());
		return extendableBean;
	}

}
