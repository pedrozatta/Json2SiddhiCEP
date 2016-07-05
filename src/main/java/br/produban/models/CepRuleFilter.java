package br.produban.models;

/**
 * Created by bera on 23/06/16.
 */

public class CepRuleFilter {

	private String field;

	private String operator;

	private String value;

	private String condition;

	public CepRuleFilter() {
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
