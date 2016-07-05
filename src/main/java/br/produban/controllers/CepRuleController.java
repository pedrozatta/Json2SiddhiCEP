package br.produban.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.produban.models.CepRule;
import br.produban.repositories.CepRuleMongoRepository;

/**
 * Created by bera on 30/06/16.
 */

@RestController
@RequestMapping("/ceprule")
public class CepRuleController {
	
	@Autowired
	private CepRuleMongoRepository cepRuleRepository;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public Iterable<CepRule> listCepRules() {
		return cepRuleRepository.findAll();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public CepRule getCepRule(@PathVariable("id") Long id) {
		return cepRuleRepository.findOne(id);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public CepRule createCepRule(@RequestBody CepRule cepRule) {
		CepRule value = cepRuleRepository.save(new CepRule(cepRule.getCepRuleId(), cepRule.getCreatedBy(),
				cepRule.getChangedBy(), cepRule.getTool(), null));
		return value;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateCepRule(@PathVariable("id") Long id, @RequestBody CepRule cepRule) {
		CepRule existingCepRule = cepRuleRepository.findOne(id);
		existingCepRule.setCepRuleId(cepRule.getCepRuleId());
		existingCepRule.setCreatedBy(cepRule.getChangedBy());
		existingCepRule.setChangedBy(cepRule.getChangedBy());
		existingCepRule.setTool(cepRule.getTool());
		existingCepRule.setFilters(cepRule.getFilters());
		cepRuleRepository.save(existingCepRule);
	}

	@RequestMapping(value = "/zatta", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String greetingJson(@RequestBody String json) {
		System.out.println("json = " + json);
		return "{ \"msg\" : \"Hello World!\"}";
	}
}
