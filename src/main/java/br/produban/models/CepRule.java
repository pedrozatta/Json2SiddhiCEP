package br.produban.models;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

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

	@NotNull
	@Size(min = 1, max = 50)
	private String changedBy;

	@NotNull
	@Size(min = 1, max = 90)
	private String tool;

	@Deprecated
	@Field("filters")
	private List<CepRuleFilter> filters;

	@Field("childs")
	private List<CepRuleFilter> childs;

	public CepRule(String cepRuleId, String createdBy, String changedBy, String tool, List<CepRuleFilter> filters) {
		super();
		this.cepRuleId = cepRuleId;
		this.createdBy = createdBy;
		this.changedBy = changedBy;
		this.tool = tool;
		this.filters = filters;
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

	public List<CepRuleFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<CepRuleFilter> filters) {
		this.filters = filters;
	}

	public List<CepRuleFilter> getChilds() {
		return childs;
	}

	public void setChilds(List<CepRuleFilter> childs) {
		this.childs = childs;
	}

}
