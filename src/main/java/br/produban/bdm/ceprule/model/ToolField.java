package br.produban.bdm.ceprule.model;

import java.io.Serializable;
import java.util.List;

import br.produban.bdm.ceprule.enumeration.FieldType;

public class ToolField implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private FieldType type;
	private List<String> values;

	public ToolField() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

}
