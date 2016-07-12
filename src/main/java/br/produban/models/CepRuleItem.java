package br.produban.models;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by pedrozatta
 */

public class CepRuleItem {

	private String field;

	private String fieldType;

	private String operator;

	private String valueMin;

	private String valueMax;

	private String condition;

	private String type;

	private Long id;
	
	private Long nivel;
	
	private Boolean disabled;

	@Field("childs")
	private List<CepRuleItem> childs;

	public CepRuleItem() {
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
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

	public Long getNivel() {
		return nivel;
	}

	public void setNivel(Long nivel) {
		this.nivel = nivel;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public List<CepRuleItem> getChilds() {
		return childs;
	}

	public void setChilds(List<CepRuleItem> childs) {
		this.childs = childs;
	}

	public String getValueMin() {
		return valueMin;
	}

	public void setValueMin(String valueMin) {
		this.valueMin = valueMin;
	}

	public String getValueMax() {
		return valueMax;
	}

	public void setValueMax(String valueMax) {
		this.valueMax = valueMax;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

}
