package br.produban.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.cache.annotation.CacheResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.produban.models.Tool;
import br.produban.models.ToolField;
import br.produban.ws.EventStreamAdminServiceClient;

/**
 * Created by pedrozatta
 */

@Service
public class MasterDataService {

	final static Logger logger = Logger.getLogger(MasterDataService.class);

	@Value("${br.produban.camel.endpoint.metricsByTool}")
	public String endpoint;

	@Autowired
	public EventStreamAdminServiceClient eventStreamAdminServiceClient;

	public List<String> findMetricsByTool(final String toolId) {
		Tool tool = this.findById(toolId);
		return findMetricsByTool(tool);
	}

	public List<String> findMetricsByTool(final Tool tool) {
		String toolEndpoint = endpoint.replace("{tool}", tool.getNickName());
		logger.info(toolEndpoint);

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("tools/zabbix.json");

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			String[] list = mapper.readValue(inputStream, String[].class);
			return Arrays.asList(list);
		} catch (IOException e) {
		}

		return null;

	}

	public String findMetricsByToolOld(final String tool) {

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("tools/zabbix.json");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");

		String json = scanner.hasNext() ? scanner.next() : "";

		return json;

	}

	public List<Tool> findTools() {

		List<Tool> result = new ArrayList<Tool>();
		List<String> list = eventStreamAdminServiceClient.getStreamNames();
		for (String item : list) {
			if (item.startsWith("IN")) {
				result.add(findById(item));
			}
		}
		return result;

	}

	@CacheResult(cacheName = "cache-tool")
	public Tool findById(String id) {
		String jsonTool = eventStreamAdminServiceClient.getStreamDetailsForStreamId(id);
		return createTool(id, jsonTool);
	}

	protected Tool createTool(String id, String jsonTool) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Tool tool = null;
		try {
			tool = mapper.readValue(jsonTool, Tool.class);
			List<String> metrics = findMetricsByTool(tool);
			for (ToolField field : tool.getFields()) {
				if ("metric".equals(field.getName())) {
					field.setValues(metrics);
				}
			}
		} catch (IOException e) {
			tool = new Tool();
		}
		tool.setId(id);
		tool.setJson(jsonTool);

		return tool;
	}

}
