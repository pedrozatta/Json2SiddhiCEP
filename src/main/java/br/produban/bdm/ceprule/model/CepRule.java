package br.produban.bdm.ceprule.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;
import org.springframework.util.CollectionUtils;

import br.produban.bdm.ceprule.enumeration.ItemType;

/**
 * Created by bera on 23/06/16.
 */

@Region("CepRule")
public class CepRule implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String WINDOW_LENGTH = "window.length";
	public static final String FIELD_VALUE = "value";

	@Id
	private String cepRuleId;

	private String ruleName;

	private String createdBy;

	private Date createdDate;

	private String changedBy;

	private Date changedDate;

	private String removedBy;

	private Date removedDate;

	private Tool tool;

	private String siddhi;

	private String situation;

	private Long nivel;

	private List<CepRuleItem> children;

	private List<MessageItem> message;

	private List<ToolBox> toolBox;

	private List<ToolField> groupBy;

	private Boolean removed;

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

	public String getSiddhi() {
		return siddhi;
	}

	public void setSiddhi(String siddhi) {
		this.siddhi = siddhi;
	}

	public ToolBox getToolBox(String name) {
		if (CollectionUtils.isEmpty(this.getToolBox())) {
			return null;
		}
		for (ToolBox item : this.getToolBox()) {
			if (name.equals(item.getName())) {
				return item;
			}
		}
		return null;
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
		}
		if (field.equals(cepRuleItem.getField())) {
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

	public List<ToolBox> getToolBox() {
		return toolBox;
	}

	public void setToolBox(List<ToolBox> toolBox) {
		this.toolBox = toolBox;
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

}
