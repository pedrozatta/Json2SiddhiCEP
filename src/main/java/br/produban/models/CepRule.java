package br.produban.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import br.produban.enumerations.ItemType;

/**
 * Created by bera on 23/06/16.
 */

public class CepRule implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String cepRuleId;

	private String ruleName;

	private String createdBy;

	private Date createdDate;

	private String changedBy;

	private Date changedDate;

	private String removedBy;

	private Date removedDate;

	private String tool;

	@Field("children")
	private List<CepRuleItem> children;

	private String siddhi;

	private String situation;

	private List<MessageItem> message;

	public CepRule(String cepRuleId, String createdBy, String changedBy, String tool, List<CepRuleItem> children) {
		super();
		this.cepRuleId = cepRuleId;
		this.createdBy = createdBy;
		this.changedBy = changedBy;
		this.tool = tool;
		this.children = children;
	}

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

	public String getTool() {
		return tool;
	}

	public void setTool(String tool) {
		this.tool = tool;
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

	public CepRuleItem getField(String field) {
		CepRuleItem cepRuleItem = getField(field, this.getChildren());
		return cepRuleItem;
	}

	protected CepRuleItem getField(String field, Iterable<CepRuleItem> items) {
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

}
