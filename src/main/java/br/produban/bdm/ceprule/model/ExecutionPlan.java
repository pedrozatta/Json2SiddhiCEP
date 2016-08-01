package br.produban.bdm.ceprule.model;

import java.io.Serializable;

public class ExecutionPlan implements Serializable {

	private static final long serialVersionUID = 1L;

	private String plan;
	private String message;
	private Boolean valid;

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}
}
