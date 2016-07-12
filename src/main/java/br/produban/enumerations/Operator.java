package br.produban.enumerations;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by pedrozatta
 */

public enum Operator {

	BETWEEN("between"), EQUAL("=="), NOT_EQUAL("!="), LESS_THAN("<"), GREATER_THAN(">"), LESS_THAN_EQUAL(
			"<="), GREATER_THAN_EQUAL(">=");

	public final String external;

	private Operator(String external) {
		this.external = external;
	}

	public static Operator fromExternal(String external) {
		if (StringUtils.isEmpty(external)) {
			throw new IllegalArgumentException();
		}
		for (Operator itemType : Operator.values()) {
			if (itemType.external.equals(external)) {
				return itemType;
			}
		}
		throw new IllegalArgumentException();
	}

}
