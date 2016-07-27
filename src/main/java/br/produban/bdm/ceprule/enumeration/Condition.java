package br.produban.bdm.ceprule.enumeration;

import org.apache.commons.lang3.Validate;

/**
 * Created by pedrozatta
 */

public enum Condition {

	AND("AND"), OR("OR");

	public final String external;

	private Condition(String external) {
		this.external = external;
	}

	public static Condition fromExternal(String external) {
		Validate.notEmpty(external);
		for (Condition itemType : Condition.values()) {
			if (itemType.external.equals(external)) {
				return itemType;
			}
		}
		throw new IllegalArgumentException();
	}

}
