package br.produban.enumerations;

import org.junit.Assert;
import org.junit.Test;

public class ConditionTest {

	@Test(expected = IllegalArgumentException.class)
	public void testFromExternalNull() {
		Condition.fromExternal(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromExternalInvalid() {
		Condition.fromExternal("invalid");
	}

	@Test
	public void testFromExternalString() {
		Condition result = Condition.fromExternal("AND");
		Assert.assertNotNull(result);
		Assert.assertEquals(Condition.AND, result);
	}

	@Test
	public void testFromExternalBetween() {
		Condition result = Condition.fromExternal("OR");
		Assert.assertNotNull(result);
		Assert.assertEquals(Condition.OR, result);
	}

}
