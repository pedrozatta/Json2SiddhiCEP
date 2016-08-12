package br.produban.bdm.ceprule.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.util.CollectionUtils;

import br.produban.bdm.ceprule.enumeration.ItemType;

/**
 * Created by bera on 23/06/16.
 */

public class CepRule implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FIELD_VALUE = "value";

	@Id
	private String cepRuleId;

	private String ruleName;

	private String description;

	private String createdBy;

	private Date createdDate;

	private String changedBy;

	private Date changedDate;

	private String removedBy;

	private Date removedDate;

	private Tool tool;

	private String situation;

	private Long nivel;

	private List<CepRuleItem> children;

	private List<MessageItem> message;

	private ToolBox window;

	private List<ToolField> groupBy;

	private Boolean removed;

	private ExecutionPlan plan;

	public CepRule() {
	}

	public String getCepRuleId() {
		return cepRuleId;
	}

	public void setCepRuleId(String cepRuleId) {
		this.cepRuleId = cepRuleId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(String changedBy) {
		this.changedBy = changedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(Date changedDate) {
		this.changedDate = changedDate;
	}

	public CepRuleItem getField(String field) {
		CepRuleItem cepRuleItem = getField(field, this.getChildren());
		return cepRuleItem;
	}

	protected CepRuleItem getField(String field, List<CepRuleItem> items) {
		if (CollectionUtils.isEmpty(items)) {
			return null;
		}
		for (CepRuleItem item : items) {
			CepRuleItem result = getField(field, item);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	protected CepRuleItem getField(String field, CepRuleItem cepRuleItem) {

		if (ItemType.fromExternal(cepRuleItem.getType()) == ItemType.GROUP) {
			CepRuleItem result = getField(field, cepRuleItem.getChildren());
			if (result != null) {
				return result;
			}
		} else if (field.equals(cepRuleItem.getField().getName())) {
			return cepRuleItem;
		}
		return null;
	}

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public List<MessageItem> getMessage() {
		return message;
	}

	public void setMessage(List<MessageItem> message) {
		this.message = message;
	}

	public String getRemovedBy() {
		return removedBy;
	}

	public void setRemovedBy(String removedBy) {
		this.removedBy = removedBy;
	}

	public Date getRemovedDate() {
		return removedDate;
	}

	public void setRemovedDate(Date removedDate) {
		this.removedDate = removedDate;
	}

	public List<CepRuleItem> getChildren() {
		return children;
	}

	public void setChildren(List<CepRuleItem> children) {
		this.children = children;
	}

	public Long getNivel() {
		return nivel;
	}

	public void setNivel(Long nivel) {
		this.nivel = nivel;
	}

	public List<ToolField> getGroupBy() {
		return groupBy;
	}

	public void setGroupBy(List<ToolField> groupBy) {
		this.groupBy = groupBy;
	}

	public Tool getTool() {
		return tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ExecutionPlan getPlan() {
		return plan;
	}

	public void setPlan(ExecutionPlan plan) {
		this.plan = plan;
	}

	public ToolBox getWindow() {
		return window;
	}

	public void setWindow(ToolBox window) {
		this.window = window;
	}

}
