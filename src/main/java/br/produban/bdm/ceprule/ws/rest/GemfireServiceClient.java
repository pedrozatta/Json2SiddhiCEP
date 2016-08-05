package br.produban.bdm.ceprule.ws.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import br.produban.bdm.ceprule.model.CepRule;

/**
 * Created by pedrozatta
 */

@Service
public class GemfireServiceClient {

	protected String endpointRegion = "http://srvbigpvlbr10.bs.br.bsch:8282/gemfire-api/v1/{region}";
	protected String endpointRegionKey = "http://srvbigpvlbr10.bs.br.bsch:8282/gemfire-api/v1/{region}/{key}";

	protected String region = "CepRule";

	protected final ObjectMapper mapper;

	public GemfireServiceClient() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleModule module = new SimpleModule();
		module.addDeserializer(RegionCepRule.class, new RegionCepRuleDeserializer());
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

			String result = restTemplate.getForObject(endpointRegion, String.class, vars);
			RegionCepRule regionCepRule = mapper.readValue(result, RegionCepRule.class);

			return regionCepRule.getList();

		} catch (HttpClientErrorException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class RegionCepRule {
		private String name;
		private List<CepRule> list;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<CepRule> getList() {
			return list;
		}

		public void setList(List<CepRule> list) {
			this.list = list;
		}
	}

	public static class Region<T> {
		private String name;
		private List<T> list;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<T> getList() {
			return list;
		}

		public void setList(List<T> list) {
			this.list = list;
		}
	}

	public static class RegionCepRuleDeserializer extends JsonDeserializer<RegionCepRule> {

		private final CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class,
				CepRule.class);

		@Override
		public RegionCepRule deserialize(JsonParser jp, DeserializationContext ctx)
				throws IOException, JsonProcessingException {

			jp.nextValue();

			RegionCepRule result = new RegionCepRule();
			result.setName(jp.getCurrentName());

			List<CepRule> list = jp.getCodec().readValue(jp, collectionType);
			result.setList(list);

			return result;
		}
	}

	public static class RegionDeserializer extends JsonDeserializer<RegionCepRule> {

		private final CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class,
				CepRule.class);

		@Override
		public RegionCepRule deserialize(JsonParser jp, DeserializationContext ctx)
				throws IOException, JsonProcessingException {

			jp.nextValue();

			RegionCepRule result = new RegionCepRule();
			result.setName(jp.getCurrentName());

			List<CepRule> list = jp.getCodec().readValue(jp, collectionType);
			result.setList(list);

			return result;
		}
	}

	public static class RegionCepRuleDeserializerBkp extends JsonDeserializer<RegionCepRule> {

		private final ObjectMapper mapper = new ObjectMapper();

		@SuppressWarnings("all")
		@Override
		public RegionCepRule deserialize(JsonParser jp, DeserializationContext ctx)
				throws IOException, JsonProcessingException {
			RegionCepRule result = new RegionCepRule();
			// JsonToken tokem = jp.nextValue();
			// String name = jp.getCurrentName();
			// Iterator<CepRule> iterator = jp.readValuesAs(CepRule.class);
			// Object o = IteratorUtils.toList(iterator);

			JsonNode node = jp.readValueAsTree();
			JsonNode node1 = node.get("CepRule");
			CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class,
					CepRule.class);

			Object o = mapper.reader(collectionType).readValue(node1);

			// JsonNode node = jp.readValueAsTree();
			// JsonNode node1 = node.get("CepRule");
			// Iterator<CepRule> iterator = jp.readValuesAs(CepRule.class);
			// result.setList(IteratorUtils.toList(iterator));

			return result;
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
		return null;
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