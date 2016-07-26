package br.produban.controllers;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.produban.services.MasterDataService;

/**
 * Created by pedrozatta
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/masterdata")
public class MasterDataController {

	final static Logger logger = Logger.getLogger(MasterDataController.class);

	@Autowired
	protected MasterDataService masterDataService;

	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

	@RequestMapping(value = "/{tool}/metrics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> findMetricsByToll(@PathVariable("tool") String tool) {
		return masterDataService.findMetricsByTool(tool);
	}

}
