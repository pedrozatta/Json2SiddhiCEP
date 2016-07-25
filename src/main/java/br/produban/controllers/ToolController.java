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
@RequestMapping("/tool")
public class ToolController {

	final static Logger logger = Logger.getLogger(ToolController.class);

	@Autowired
	protected MasterDataService masterDataService;

	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

	@RequestMapping(value = "/{id}/metrics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String findMetricsByToll(@PathVariable("id") String id) {
		return masterDataService.findMetricsByTool(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String findToolById(@PathVariable("id") String id) {
		return masterDataService.findMetricsByTool(id);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> findToolById() {
		return masterDataService.findTools();
	}

}
