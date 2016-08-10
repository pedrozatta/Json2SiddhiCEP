package br.produban.bdm.ceprule.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pedrozatta
 */

public class CepRuleItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private ToolField field;

	private String operator;

	private String valueMin;

	private String valueMax;

	private String condition;

	private String type;

	private Long id;

	private Long nivel;

	private Boolean disabled;

	private Boolean disabledRowGroup;

	private String conditionGroup;

	private List<CepRuleItem> children;

	public CepRuleItem() {
	}

	public ToolField getField() {
		return field;
	}

	public void setField(ToolField field) {
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

	public String getConditionGroup() {
		return conditionGroup;
	}

	public void setConditionGroup(String conditionGroup) {
		this.conditionGroup = conditionGroup;
	}

	public List<CepRuleItem> getChildren() {
		return children;
	}

	public void setChildren(List<CepRuleItem> children) {
		this.children = children;
	}

	public Boolean getDisabledRowGroup() {
		return disabledRowGroup;
	}

	public void setDisabledRowGroup(Boolean disabledRowGroup) {
		this.disabledRowGroup = disabledRowGroup;
	}

}
