package br.produban.bdm.ceprule.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.produban.bdm.ceprule.model.Tool;
import br.produban.bdm.ceprule.service.ToolService;

/**
 * Created by pedrozatta
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/tool")
public class ToolController {

	final static Logger logger = Logger.getLogger(ToolController.class);

	@Autowired
	protected ToolService masterDataService;

	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

	@RequestMapping(value = "/{id}/metrics", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> findMetricsByToll(@PathVariable("id") String id) {
		return masterDataService.findMetricsByTool(id);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Tool findById(@RequestParam(value = "id") String id) {
		Tool tool = masterDataService.findById(id);
		return tool;
	}

	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Tool> find() {
		List<Tool> list = masterDataService.findTools();
		for (Tool tool : list) {
			tool.setFields(null);
		}
		return list;
	}

}
