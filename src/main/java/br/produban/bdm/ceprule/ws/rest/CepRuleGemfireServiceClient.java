package br.produban.bdm.ceprule.ws.rest;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
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
import com.fasterxml.jackson.databind.type.TypeFactory;

import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.commons.gemfire.Region;

/**
 * Created by pedrozatta
 */

@Service
public class CepRuleGemfireServiceClient {

	final static Logger logger = Logger.getLogger(CepRuleGemfireServiceClient.class);

	protected String endpointRegion = "http://srvbigpvlbr10.bs.br.bsch:8282/gemfire-api/v1/{region}";
	protected String endpointRegionKey = "http://srvbigpvlbr10.bs.br.bsch:8282/gemfire-api/v1/{region}/{key}";
	protected String endpointForUpdate = "http://srvbigpvlbr10.bs.br.bsch:8282/gemfire-api/v1/{region}/{key}?op=PUT";

	protected String region = "CepRule";

	protected final ObjectMapper mapper;

	public CepRuleGemfireServiceClient() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Region.class, new CepRuleGemfireServiceDeserializer());
		mapper.registerModule(module);
	}

	public List<CepRule> findBySituationStartingWith(@Param("situation") String situation) {
		return null;
	}

	public List<CepRule> findByRemoved(@Param("removed") Boolean removed) {
		return null;
	}

	public List<CepRule> findAll() {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("region", region);

		RestTemplate restTemplate = new RestTemplate();
		try {

			JavaType javaType = TypeFactory.defaultInstance().constructType(Region.class, CepRule.class);

			String result = restTemplate.getForObject(endpointRegion, String.class, vars);
			Region<CepRule> regionCepRule = mapper.readValue(result, javaType);

			return regionCepRule.getList();

		} catch (HttpClientErrorException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public CepRule findOne(String key) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("region", region);
		vars.put("key", key);

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<CepRule> result = restTemplate.getForEntity(endpointRegionKey, CepRule.class, vars);
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
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("region", region);

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<Void> result = restTemplate.postForEntity(endpointRegion, cepRule, Void.class, vars);
			URI uri = result.getHeaders().getLocation();
			String id = uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
			cepRule.setCepRuleId(id);
			return cepRule;
		} catch (HttpClientErrorException e) {
			throw new RuntimeException(e);
		}
	}

	protected CepRule update(CepRule cepRule) {
		Map<String, Object> vars = new HashMap<String, Object>();
		vars.put("region", region);
		vars.put("key", cepRule.getCepRuleId());

		RestTemplate restTemplate = new RestTemplate();
		try {
			restTemplate.put(endpointForUpdate, cepRule, vars);
			return cepRule;
		} catch (HttpClientErrorException e) {
			throw new RuntimeException(e);
		}
	}

	public long count() {
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("region", region);
		RestTemplate restTemplate = new RestTemplate();
		try {
			HttpHeaders result = restTemplate.headForHeaders(endpointRegion, vars);
			return Long.valueOf(result.get("Resource-Count").get(0));

		} catch (HttpClientErrorException e) {
			throw new RuntimeException(e);
		}
	}

	// public CepRule save(CepRule cepRule) {
	// return null;
	// }

}