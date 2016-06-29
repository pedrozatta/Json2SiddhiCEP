package br.produban.Entities;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;

/**
 * Created by bera on 23/06/16.
 */
public class Rule {

    private final int id;

    private String ruleId;

    private String createdByUser;

    private String editedByUser;

    private String tool;

    private HashMap<String,String> ruleFilters;

    public int getId() {
        return id;
    }



    public Rule(String ruleId, String createdByUser,
                String editedByUser, String tool) {
        this.id = -1;
        this.ruleId = ruleId;
        this.createdByUser = createdByUser;
        this.editedByUser = editedByUser;
        this.tool = tool;
        this.ruleFilters = new HashMap<String,String>();
    }

    public Rule(JsonObject json) {
        this.id = json.getInteger("ID");
        this.ruleId = json.getString("RULEID");
        this.createdByUser = json.getString("CREATEDBYUSER");
        this.editedByUser = json.getString("EDITEDBYUSER");
        this.tool = json.getString("TOOL");
        //this.ruleFilters = new HashMap<String,String>();
    }

    public Rule() {
        this.id = -1;
    }

    public Rule(int id, String ruleId, String createdByUser,
                String editedByUser, String tool) {
        this.id = id;
        this.ruleId = ruleId;
        this.createdByUser = createdByUser;
        this.editedByUser = editedByUser;
        this.tool = tool;
        this.ruleFilters = new HashMap<String,String>();
    }

    public HashMap<String, String> getRuleFilters() {
        return ruleFilters;
    }

    public void setRuleFilters(HashMap<String, String> ruleFilters) {
        this.ruleFilters = ruleFilters;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getEditedByUser() {
        return editedByUser;
    }

    public void setEditedByUser(String editedByUser) {
        this.editedByUser = editedByUser;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String created_by_user) {
        this.createdByUser = created_by_user;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

}
