package br.produban.services;

import java.io.InputStream;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Created by pedrozatta
 */

@Service
public class MasterDataService {

	final static Logger logger = Logger.getLogger(MasterDataService.class);

	public String findMetricsByTool(final String tool) {

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("tools/zabbix.json");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");

		String json = scanner.hasNext() ? scanner.next() : "";

		return json;

	}

}
