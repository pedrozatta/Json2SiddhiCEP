package br.produban.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.produban.ws.EventStreamAdminServiceClient;
import cep.wsdl.EventStreamInfoDto;

/**
 * Created by pedrozatta
 */

@Service
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

	public List<String> findTools() {

		List<String> result = new ArrayList<String>();
		List<EventStreamInfoDto> list = eventStreamAdminServiceClient.getAllEventStreamDefinitionDto();
		for (EventStreamInfoDto item : list) {
			result.add(item.getStreamDefinition().getValue());
		}

		return result;

	}

}
