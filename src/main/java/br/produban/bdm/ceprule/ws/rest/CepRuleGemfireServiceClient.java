package br.produban.bdm.ceprule.ws.rest;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.commons.ExtendableBean;
import br.produban.bdm.commons.Region;

/**
 * Created by pedrozatta
 */

@Service
public class CepRuleGemfireServiceClient {

	final static Logger logger = Logger.getLogger(CepRuleGemfireServiceClient.class);

	@Value("${br.produban.gemfire.endpoint.CepRule}")
	protected String endpoint;

	@Value("${br.produban.gemfire.endpoint.CepRuleBySituationStartingWith}")
	protected String endpointCepRuleBySituationStartingWith;

	@Value("${br.produban.gemfire.endpoint.CepRuleByRemoved}")
	protected String endpointCepRuleByRemoved;

	@Value("${br.produban.gemfire.endpoint.queries}")
	protected String endpointQueries;

	protected final ObjectMapper mapper;

	protected final CollectionType collectionType;

	public CepRuleGemfireServiceClient() {
		collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, CepRule.class);

		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Region.class, new CepRuleGemfireServiceDeserializer());
		mapper.registerModule(module);
	}

	public List<CepRule> findAll() {
		Map<String, Object> vars = new HashMap<String, Object>();

		RestTemplate restTemplate = new RestTemplate();
		try {

			JavaType javaType = TypeFactory.defaultInstance().constructType(Region.class, CepRule.class);

			String result = restTemplate.getForObject(endpoint, String.class, vars);
			Region<CepRule> regionCepRule = mapper.readValue(result, javaType);

			return regionCepRule.getList();

		} catch (HttpClientErrorException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public CepRule findOne(String key) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("key", key);

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<CepRule> result = restTemplate.getForEntity(endpoint + "/{key}", CepRule.class, vars);
			return result.getBody();
		} catch (HttpClientErrorException e) {
			throw new RuntimeException(e);
		}
	}

	public CepRule save(CepRule cepRule) {
		if (StringUtils.isEmpty(cepRule.getCepRuleId())) {
			return this.create(cepRule);
		} else {
			return this.update(cepRule);
		}
	}

	protected CepRule create(CepRule cepRule) {

		RestTemplate restTemplate = new RestTemplate();
		try {
			URI uri = restTemplate.postForLocation(endpoint, new ExtendableBean());
			String id = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
			cepRule.setCepRuleId(id);
			return this.update(cepRule);
		} catch (HttpClientErrorException e) {
			throw new RuntimeException(e);
		}

	}

	protected CepRule update(CepRule cepRule) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("key", cepRule.getCepRuleId());

		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.put(endpoint + "/{key}?op=PUT", cepRule, vars);
			return cepRule;
		} catch (HttpClientErrorException e) {
			throw new RuntimeException(e);
		}
	}

	public long count() {
		Map<String, String> vars = new HashMap<String, String>();
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders result = restTemplate.headForHeaders(endpoint, vars);
			return Long.valueOf(result.get("Resource-Count").get(0));

		} catch (HttpClientErrorException e) {
			throw new RuntimeException(e);
		}
	}

	public List<CepRule> findBySituationStartingWith(@Param("situation") String situation) {

		RestTemplate restTemplate = new RestTemplate();
		try {

			ExtendableBean bean = new ExtendableBean();
			bean.add("@type", "string");
			bean.add("@value", situation + "%");

			String result = restTemplate.postForObject(endpointCepRuleBySituationStartingWith, bean, String.class);

			List<CepRule> list = mapper.readValue(result, collectionType);
			return list;

		} catch (HttpClientErrorException | IOException e) {
			throw new RuntimeException(e);
		}

	}

	public List<CepRule> findByRemoved(@Param("removed") Boolean removed) {

		ExtendableBean bean = new ExtendableBean();
		bean.add("@type", "boolean");
		bean.add("@value", removed);

		RestTemplate restTemplate = new RestTemplate();
		try {

			String result = restTemplate.postForObject(endpointCepRuleByRemoved, bean, String.class);
			List<CepRule> list = mapper.readValue(result, collectionType);

			return list;
		} catch (HttpClientErrorException | IOException e) {
			throw new RuntimeException(e);
		}
	}

}