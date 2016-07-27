package br.produban.bdm.ceprule.model;

import java.io.Serializable;

public class MessageItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;

	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
