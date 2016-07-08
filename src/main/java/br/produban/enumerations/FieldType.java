package br.produban.enumerations;

import org.apache.commons.lang3.StringUtils;

public enum FieldType {

	STRING("string"), DOUBLE("double");

	public String external;

	private FieldType(String external) {
		this.external = external;
	}

	public static FieldType fromExternal(String external) {
		if (StringUtils.isEmpty(external)) {
			throw new IllegalArgumentException("External " + external);
		}
		for (FieldType itemType : FieldType.values()) {
			if (itemType.external.equals(external)) {
				return itemType;
			}
		}
		throw new IllegalArgumentException();
	}

}
