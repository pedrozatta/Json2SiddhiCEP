package br.produban.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by bera on 23/06/16.
 */

@Entity
public class CepRule {

    @Id
    @GeneratedValue
    private Long id;


    @NotNull
    @Size(min = 1, max = 90)
    private String ruleid;

    @NotNull
    @Size(min = 1, max = 50)
    private String createdBy;

    @NotNull
    @Size(min = 1, max = 50)
    private String changedBy;

    @NotNull
    @Size(min = 1, max = 90)
    private String tool;

    private String filters;

    public CepRule() {
    }

    public CepRule(Long id, String ruleid, String createdBy, String changedBy, String tool) {
        this.id = id;
        this.ruleid = ruleid;
        this.createdBy = createdBy;
        this.changedBy = changedBy;
        this.tool = tool;
    }

    public CepRule(Long id, String filters, String tool, String changedBy, String createdBy, String ruleid) {
        this.id = id;
        this.filters = filters;
        this.tool = tool;
        this.changedBy = changedBy;
        this.createdBy = createdBy;
        this.ruleid = ruleid;
    }

    public String getRuleid() {
        return ruleid;
    }

    public void setRuleid(String ruleid) {
        this.ruleid = ruleid;
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

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }
}
