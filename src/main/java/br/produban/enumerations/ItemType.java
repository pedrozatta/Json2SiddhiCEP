package br.produban.enumerations;

import org.apache.commons.lang3.StringUtils;

public enum ItemType {

	CONDITION("condicao"), GROUP("grupo");

	public final String external;

	private ItemType(String external) {
		this.external = external;
	}

	public static ItemType fromExternal(String external) {
		if (StringUtils.isEmpty(external)) {
			throw new IllegalArgumentException();
		}
		for (ItemType itemType : ItemType.values()) {
			if (itemType.external.equals(external)) {
				return itemType;
			}
		}
		throw new IllegalArgumentException();
	}

}
