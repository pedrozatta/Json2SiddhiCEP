package br.produban.models;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by bera on 23/06/16.
 */

public class CepRuleItem {

	public static final String TYPE_CONDITION = "condicao";
	public static final String TYPE_GROUP = "grupo";

	public static final String CONDITION_AND = "E";
	public static final String CONDITION_OR = "OU";

	public static final String OPERATOR_BETWEEN = "between";

	private String field;

	private String operator;

	private String valueMin;

	private String valueMax;

	private String condition;

	private String type;

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

}
