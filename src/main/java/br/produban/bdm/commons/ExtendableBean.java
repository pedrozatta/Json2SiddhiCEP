package br.produban.bdm.commons;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class ExtendableBean {

	public ExtendableBean() {
		properties = new HashMap<String, Object>();
	}

	public void add(String key, Object value) {
		properties.put(key, value);
	}

	private Map<String, Object> properties;

	@JsonAnyGetter
	public Map<String, Object> getProperties() {
		return properties;
	}

}