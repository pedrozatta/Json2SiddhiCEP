package br.produban.enumerations;

import org.apache.commons.lang3.Validate;

/**
 * Created by pedrozatta
 */

public enum ItemType {

	CONDITION("condicao"), GROUP("grupo");

	public final String external;

	private ItemType(String external) {
		this.external = external;
	}

	public static ItemType fromExternal(String external) {
		Validate.notEmpty(external);
		for (ItemType itemType : ItemType.values()) {
			if (itemType.external.equals(external)) {
				return itemType;
			}
		}
		throw new IllegalArgumentException();
	}

}
