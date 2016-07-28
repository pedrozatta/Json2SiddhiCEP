package br.produban.bdm.ceprule.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.produban.bdm.ceprule.model.CepRule;

/**
 * Created by pedrozatta
 */

public interface CepRuleRepository extends CrudRepository<CepRule, String> {

	public List<CepRule> findBySituationStartingWith(@Param("situation") String situation);

	public List<CepRule> findByRemoved(@Param("removed") Boolean removed);

}