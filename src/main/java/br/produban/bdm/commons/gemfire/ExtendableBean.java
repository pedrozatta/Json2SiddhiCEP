package br.produban.bdm.commons.gemfire;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class ExtendableBean {

	public ExtendableBean() {
		properties = new HashMap<String, String>();
	}

	public void add(String key, String value) {
		properties.put(key, value);
	}

	private Map<String, String> properties;

	@JsonAnyGetter
	public Map<String, String> getProperties() {
		return properties;
	}

}