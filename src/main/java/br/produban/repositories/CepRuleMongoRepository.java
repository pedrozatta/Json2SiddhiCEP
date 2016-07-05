package br.produban.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import br.produban.models.CepRule;

@RepositoryRestResource(collectionResourceRel = "people", path = "people")
public interface CepRuleMongoRepository extends MongoRepository<CepRule, String> {

	List<CepRule> findByTool(@Param("tool") String tool);

}