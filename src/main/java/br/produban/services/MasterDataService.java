package br.produban.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.cache.annotation.CacheResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import br.produban.models.Tool;
import br.produban.ws.EventStreamAdminServiceClient;
import cep.wsdl.EventStreamInfoDto;

/**
 * Created by pedrozatta
 */

@Service
@CacheConfig(cacheNames = "cacheTool")
public class MasterDataService {

	final static Logger logger = Logger.getLogger(MasterDataService.class);

	@Autowired
	public EventStreamAdminServiceClient eventStreamAdminServiceClient;

	public String findMetricsByTool(final String tool) {

		teste();

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("tools/zabbix.json");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");

		String json = scanner.hasNext() ? scanner.next() : "";

		return json;

	}

	public List<String> teste() {

		List<String> result = new ArrayList<String>();
		List<String> list = eventStreamAdminServiceClient.getStreamNames();
		for (String item : list) {
			if (item.startsWith("IN")) {
				result.add(item);
			}
		}

		return result;

	}

	public String findTools() {

		List<EventStreamInfoDto> list = eventStreamAdminServiceClient.getAllEventStreamDefinitionDto();
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (EventStreamInfoDto item : list) {
			sb.append(item.getStreamDefinition().getValue());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		return sb.toString();

	}

	@CacheResult(cacheName = "tool")
	public Tool findById(String id) {

		return new Tool(id);
	}

}
