package br.produban.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

public class UserServiceTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private Authentication authentication;

	@Mock
	private SecurityContext securityContext;

	@Spy
	@InjectMocks
	private UserService userService;

	@Before
	public void setUp() throws Exception {
		Mockito.when(authentication.getName()).thenReturn("PZATTA");
		Mockito.when(userService.getContext()).thenReturn(securityContext);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
	}

	@Test
	public void testGetContextAuthentication() {
		Authentication result = userService.getContextAuthentication();
		Assert.assertNotNull(result);
		Assert.assertEquals(authentication, result);
	}

	@Test
	@Ignore
	public void testGetAuthenticatedUserName() {
		String result = userService.getAuthenticatedUserName();
		Assert.assertNotNull(result);
		Assert.assertEquals("PZATTA", result);
	}

}
