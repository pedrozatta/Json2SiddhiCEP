package br.produban.bdm.ceprule.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.text.WordUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tool implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String version;
	private String nickName;
	private String description;

	@JsonProperty("payloadData")
	private List<ToolField> fields;

	@JsonIgnore
	private String json;

	public Tool() {
	}

	public Tool(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getDescription() {
		return description;
	}

	public String getAlias() {
		return "Entrada" + WordUtils.capitalize(this.getNickName());
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tool == false) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		final Tool otherObject = (Tool) obj;

		return new EqualsBuilder().append(this.id, otherObject.id).isEquals();
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public List<ToolField> getFields() {
		return fields;
	}

	public void setFields(List<ToolField> fields) {
		this.fields = fields;
	}

}
