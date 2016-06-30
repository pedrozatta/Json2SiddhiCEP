package br.produban.controllers;

import br.produban.models.CepRule;
import br.produban.repositories.CepRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by bera on 30/06/16.
 */


@RestController
@RequestMapping("/ceprule")
public class CepRuleController {
    @Autowired
    private CepRuleRepository cepRuleRepository;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Iterable<CepRule> listCepRules() {
        return cepRuleRepository.findAll();
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public CepRule getCepRule(@PathVariable("id") Long id) {
        return cepRuleRepository.findOne(id);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void createCepRule(@RequestBody CepRule cepRule) {
        cepRuleRepository.save(
                new CepRule(
                        cepRule.getCepRuleId(),
                        cepRule.getCreatedBy(),
                        cepRule.getChangedBy(),
                        cepRule.getTool(),
                        cepRule.getFilters()
                )
        );
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
}
