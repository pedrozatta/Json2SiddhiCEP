package br.produban.bdm.ceprule.enumeration;

import org.apache.commons.lang3.Validate;

/**
 * Created by pedrozatta
 */

public enum FieldType {

	STRING("string"), DOUBLE("double");

	public String external;

	private FieldType(String external) {
		this.external = external;
	}

	public static FieldType fromExternal(String external) {
		Validate.notEmpty(external);
		for (FieldType itemType : FieldType.values()) {
			if (itemType.external.equals(external)) {
				return itemType;
			}
		}
		throw new IllegalArgumentException();
	}

	public String getExternal() {
		return external;
	}

}
