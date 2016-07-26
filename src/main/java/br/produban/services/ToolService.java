package br.produban.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.cache.annotation.CacheResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.produban.models.Tool;
import br.produban.models.ToolField;
import br.produban.ws.EventStreamAdminServiceClient;
import br.produban.ws.rest.MetricsServiceClient;

/**
 * Created by pedrozatta
 */

@Service
public class ToolService {

	final static Logger logger = Logger.getLogger(ToolService.class);

	@Autowired
	public EventStreamAdminServiceClient eventStreamAdminServiceClient;

	@Autowired
	public MetricsServiceClient metricsServiceClient;

	public List<String> findMetricsByTool(String toolNickName) {
		return metricsServiceClient.findByTool(toolNickName);
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
			List<String> metrics = metricsServiceClient.findByTool(tool.getNickName());
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
