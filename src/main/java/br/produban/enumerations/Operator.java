package br.produban.enumerations;

import org.apache.commons.lang3.Validate;

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
		Validate.notEmpty(external);
		for (Operator itemType : Operator.values()) {
			if (itemType.external.equals(external)) {
				return itemType;
			}
		}
		throw new IllegalArgumentException();
	}

}
