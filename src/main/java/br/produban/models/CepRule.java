package br.produban.models;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by bera on 23/06/16.
 */

@Entity
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

	@Transient
	private List<Map<String, String>> filters;

	public CepRule() {
	}

	public CepRule(String cepRuleId, String createdBy, String changedBy, String tool,
			List<Map<String, String>> filters) {

		this.cepRuleId = cepRuleId;
		this.createdBy = createdBy;
		this.changedBy = changedBy;
		this.tool = tool;
		this.filters = filters;

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

	public List<Map<String, String>> getFilters() {
		return filters;
	}

	public void setFilters(List<Map<String, String>> filters) {
		this.filters = filters;
	}
}
