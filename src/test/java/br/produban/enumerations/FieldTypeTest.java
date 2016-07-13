package br.produban.enumerations;

import org.junit.Assert;
import org.junit.Test;

public class FieldTypeTest {

	@Test(expected = RuntimeException.class)
	public void testFromExternalNull() {
		FieldType.fromExternal(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromExternalInvalid() {
		FieldType.fromExternal("invalid");
	}

	@Test
	public void testFromExternalString() {
		FieldType result = FieldType.fromExternal("string");
		Assert.assertNotNull(result);
		Assert.assertEquals(FieldType.STRING, result);
	}

	@Test
	public void testFromExternalDouble() {
		FieldType result = FieldType.fromExternal("double");
		Assert.assertNotNull(result);
		Assert.assertEquals(FieldType.DOUBLE, result);
	}

}
