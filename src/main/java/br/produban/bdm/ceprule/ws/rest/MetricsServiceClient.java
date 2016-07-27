package br.produban.bdm.ceprule.ws.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MetricsServiceClient {
	@Value("${br.produban.camel.endpoint.metricsByTool}")
	protected String endpoint;

	public List<String> findByTool(String toolNickName) {

		Map<String, String> vars = new HashMap<String, String>();
		vars.put("tool", toolNickName);

		RestTemplate restTemplate = new RestTemplate();
		try {
			String result = restTemplate.getForObject(endpoint, String.class, vars);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String[] list = mapper.readValue(result, String[].class);
			return Arrays.asList(list);

		} catch (HttpClientErrorException | IOException e) {
			// TODO: return cached data
			return findMetricsByToolOld(toolNickName);
		}
	}

	@Deprecated
	public List<String> findMetricsByToolOld(final String tool) {

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("tools/zabbix.json");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");

		String json = scanner.hasNext() ? scanner.next() : "";

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String[] list = null;
		try {
			list = mapper.readValue(json, String[].class);
		} catch (IOException e) {
		}
		return Arrays.asList(list);

	}

}
