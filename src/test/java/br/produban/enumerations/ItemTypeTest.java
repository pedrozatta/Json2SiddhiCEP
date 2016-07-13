package br.produban.enumerations;

import org.junit.Assert;
import org.junit.Test;

public class ItemTypeTest {

	@Test(expected = RuntimeException.class)
	public void testFromExternalNull() {
		ItemType.fromExternal(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromExternalInvalid() {
		ItemType.fromExternal("invalid");
	}

	@Test
	public void testFromExternalCondition() {
		ItemType result = ItemType.fromExternal("condicao");
		Assert.assertNotNull(result);
		Assert.assertEquals(ItemType.CONDITION, result);
	}

	@Test
	public void testFromExternalGroup() {
		ItemType result = ItemType.fromExternal("grupo");
		Assert.assertNotNull(result);
		Assert.assertEquals(ItemType.GROUP, result);
	}

}
