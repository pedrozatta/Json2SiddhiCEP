package br.produban.enumerations;

import org.junit.Assert;
import org.junit.Test;

public class OperatorTest {

	@Test(expected = RuntimeException.class)
	public void testFromExternalNull() {
		Operator.fromExternal(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromExternalInvalid() {
		Operator.fromExternal("invalid");
	}

	@Test
	public void testFromExternalEqual() {
		Operator result = Operator.fromExternal("==");
		Assert.assertNotNull(result);
		Assert.assertEquals(Operator.EQUAL, result);
	}

	@Test
	public void testFromExternalDouble() {
		Operator result = Operator.fromExternal("<=");
		Assert.assertNotNull(result);
		Assert.assertEquals(Operator.LESS_THAN_EQUAL, result);
	}
}
