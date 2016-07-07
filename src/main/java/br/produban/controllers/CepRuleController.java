package br.produban.controllers;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.produban.models.CepRule;
import br.produban.repositories.CepRuleMongoRepository;
import br.produban.service.CepRuleService;

/**
 * Created by bera on 30/06/16.
 */

@RestController
@RequestMapping("/ceprule")
public class CepRuleController {

	final static Logger logger = Logger.getLogger(CepRuleController.class);

	@Autowired
	private CepRuleMongoRepository cepRuleRepository;

	@Autowired
	private CepRuleService cepRuleService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public Iterable<CepRule> listCepRules() {
		logger.info("listCepRules()");
		return cepRuleRepository.findAll();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public CepRule getCepRule(@PathVariable("id") String id) {
		logger.info("getCepRule() " + id);
		CepRule cepRule = cepRuleRepository.findOne(id);
		return cepRule;
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public CepRule createCepRule(@RequestBody final CepRule cepRule) {
		logger.info("createCepRule(..)");
		return save(cepRule.getChangedBy(), cepRule);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public CepRule updateCepRule(@PathVariable("id") String id, @RequestBody CepRule cepRule) {
		logger.info("updateCepRule(..)");
		CepRule value = save(cepRule.getChangedBy(), cepRule);
		return value;
	}

	protected CepRule save(final String user, final CepRule value) {
		if (StringUtils.isEmpty(user)) {
			throw new IllegalArgumentException("User can not be null");
		}
		if (value == null) {
			throw new IllegalArgumentException("CepRule can not be null");
		}
		CepRule cepRule = value;
		if (StringUtils.isEmpty(cepRule.getCepRuleId())) {
			cepRule.setCreatedDate(now());
			cepRule.setCreatedBy(user);
		} else {
			CepRule cepRuleOld = cepRuleRepository.findOne(cepRule.getCepRuleId());
			cepRule.setCreatedDate(cepRuleOld.getCreatedDate());
			cepRule.setCreatedBy(cepRuleOld.getCreatedBy());
		}
		cepRule.setChangedDate(now());
		cepRule.setChangedBy(user);

		cepRule = cepRuleRepository.save(cepRule);

		String cepRuleString = cepRuleService.processCepRule(cepRule);
		cepRule.setCepRuleString(cepRuleString);

		return cepRule;
	}

	protected Date now() {
		return Calendar.getInstance().getTime();
	}

}
