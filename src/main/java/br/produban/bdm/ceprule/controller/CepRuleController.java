package br.produban.bdm.ceprule.controller;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.ceprule.service.CepRuleService;

/**
 * Created by bera on 30/06/16.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/ceprule")
public class CepRuleController {

	final static Logger logger = Logger.getLogger(CepRuleController.class);

	@Autowired
	protected CepRuleService cepRuleService;

	@ModelAttribute
	public void setVaryResponseHeader(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Origin", "*");
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public Iterable<CepRule> listCepRules() {
		logger.info("listCepRules()");
		return cepRuleService.findAll();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public CepRule getCepRule(@PathVariable("id") String id) {
		logger.info("getCepRule() " + id);
		CepRule cepRule = cepRuleService.findOne(id);
		return cepRule;
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public CepRule createCepRule(@RequestBody final CepRule cepRule, Principal principal)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		logger.info("createCepRule(..)");
		CepRule clone = (CepRule) SerializationUtils.clone(cepRule);
		cepRuleService.save(clone);
		cepRule.setCepRuleId(clone.getCepRuleId());
		cepRule.setSituation(clone.getSituation());
		cepRule.setSiddhi(clone.getSiddhi());
		cepRule.setChangedBy(clone.getChangedBy());
		cepRule.setChangedDate(clone.getChangedDate());
		cepRule.setCreatedBy(clone.getCreatedBy());
		cepRule.setCreatedDate(clone.getCreatedDate());
		return cepRule;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public CepRule updateCepRule(@PathVariable("id") String id, @RequestBody CepRule cepRule) {
		logger.info("updateCepRule(..)");
		CepRule value = cepRuleService.save(cepRule);
		return value;
	}

	@RequestMapping(value = "/situation/{situation}", method = RequestMethod.GET)
	public Iterable<CepRule> findBySituation(@PathVariable("situation") String situation) {
		logger.info("findBySituation(..) " + situation);
		return cepRuleService.findBySituation(situation);
	}

}
