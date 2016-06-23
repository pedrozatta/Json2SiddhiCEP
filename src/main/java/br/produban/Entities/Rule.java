package br.produban.Entities;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bera on 23/06/16.
 */
public class Rule {

    private static final AtomicInteger COUNTER = new AtomicInteger();



    private final int id;

    private String ruleid;

    private String created_by_user;

    private String edited_by_user;

    private String tool;

    private HashMap<String,String> rule_filters;

    public int getId() {
        return id;
    }

    public Rule() {
        this.id = COUNTER.getAndIncrement();
    }
    public Rule(String ruleid, String created_by_user,
                String edited_by_user, String tool, HashMap<String, String> rule_filters) {
        this.id = COUNTER.getAndIncrement();
        this.ruleid = ruleid;
        this.created_by_user = created_by_user;
        this.edited_by_user = edited_by_user;
        this.tool = tool;
        this.rule_filters = rule_filters;
    }

    public HashMap<String, String> getRule_filters() {
        return rule_filters;
    }

    public void setRule_filters(HashMap<String, String> rule_filters) {
        this.rule_filters = rule_filters;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getEdited_by_user() {
        return edited_by_user;
    }

    public void setEdited_by_user(String edited_by_user) {
        this.edited_by_user = edited_by_user;
    }

    public String getCreated_by_user() {
        return created_by_user;
    }

    public void setCreated_by_user(String created_by_user) {
        this.created_by_user = created_by_user;
    }

    public String getRuleid() {
        return ruleid;
    }

    public void setRuleid(String ruleid) {
        this.ruleid = ruleid;
    }

}
