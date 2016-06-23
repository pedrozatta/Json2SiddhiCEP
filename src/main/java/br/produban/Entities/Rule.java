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

    public int getId() {
        return id;
    }

    public Rule(int id) {
        this.id = COUNTER.getAndIncrement();
    }

    public Rule(int id, String ruleid, String created_by_user, String edited_by_user, String tool, HashMap<String, String> rule_filters) {
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

    private HashMap<String,String> rule_filters;

    @Override
    public String toString() {
        return "Rule{" +
                "ruleid='" + ruleid + '\'' +
                ", tool='" + tool + '\'' +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rule rule = (Rule) o;

        if (id != rule.id) return false;
        if (!ruleid.equals(rule.ruleid)) return false;
        if (created_by_user != null ? !created_by_user.equals(rule.created_by_user) : rule.created_by_user != null)
            return false;
        if (edited_by_user != null ? !edited_by_user.equals(rule.edited_by_user) : rule.edited_by_user != null)
            return false;
        if (tool != null ? !tool.equals(rule.tool) : rule.tool != null) return false;
        return rule_filters != null ? rule_filters.equals(rule.rule_filters) : rule.rule_filters == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + ruleid.hashCode();
        result = 31 * result + (created_by_user != null ? created_by_user.hashCode() : 0);
        result = 31 * result + (edited_by_user != null ? edited_by_user.hashCode() : 0);
        result = 31 * result + (tool != null ? tool.hashCode() : 0);
        result = 31 * result + (rule_filters != null ? rule_filters.hashCode() : 0);
        return result;
    }
}
