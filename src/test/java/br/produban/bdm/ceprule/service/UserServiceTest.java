package br.produban.bdm.ceprule.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import br.produban.bdm.ceprule.commons.UserUtil;

public class UserServiceTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private UserUtil userUtil;

	@Spy
	@InjectMocks
	private UserService userService;

	@Before
	public void setUp() throws Exception {
		Mockito.when(userUtil.getAuthenticatedUserName()).thenReturn("PZATTA");
	}

	@Test
	public void testGetAuthenticatedUserName() {
		String result = userService.getAuthenticatedUserName();
		Assert.assertNotNull(result);
		Assert.assertEquals("PZATTA", result);
	}

}
