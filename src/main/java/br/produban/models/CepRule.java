package br.produban.models;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import br.produban.enumerations.ItemType;

/**
 * Created by bera on 23/06/16.
 */

public class CepRule {

	@Id
	@NotNull
	@Size(min = 1, max = 90)
	private String cepRuleId;

	@NotNull
	@Size(min = 1, max = 50)
	private String createdBy;

	private Date createdDate;

	@NotNull
	@Size(min = 1, max = 50)
	private String changedBy;

	private Date changedDate;

	@NotNull
	@Size(min = 1, max = 90)
	private String tool;

	@Field("childs")
	// @JsonProperty("childs")
	private List<CepRuleItem> children;

	private String siddhi;

	private String situation;

	public CepRule(String cepRuleId, String createdBy, String changedBy, String tool, List<CepRuleItem> filters) {
		super();
		this.cepRuleId = cepRuleId;
		this.createdBy = createdBy;
		this.changedBy = changedBy;
		this.tool = tool;
		this.children = filters;
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

//	@Deprecated
//	public List<CepRuleItem> getFilters() {
//		return children;
//	}

	@Deprecated
	public void setFilters(List<CepRuleItem> filters) {
		this.children = filters;
	}

//	@Deprecated
//	public List<CepRuleItem> getChilds() {
//		return children;
//	}

	@Deprecated
	public void setChilds(List<CepRuleItem> children) {
		this.children = children;
	}

	public List<CepRuleItem> getChildren() {
		return children;
	}

	public void setChildren(List<CepRuleItem> children) {
		this.children = children;
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

}
