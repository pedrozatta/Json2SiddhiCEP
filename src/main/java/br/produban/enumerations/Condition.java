package br.produban.enumerations;

import org.apache.commons.lang3.StringUtils;

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
		if (StringUtils.isEmpty(external)) {
			throw new IllegalArgumentException();
		}
		for (Condition itemType : Condition.values()) {
			if (itemType.external.equals(external)) {
				return itemType;
			}
		}
		throw new IllegalArgumentException();
	}

}
