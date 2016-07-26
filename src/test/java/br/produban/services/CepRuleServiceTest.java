package br.produban.services;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import br.produban.models.CepRule;
import br.produban.models.MessageItem;
import br.produban.repositories.CepRuleMongoRepository;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;

public class CepRuleServiceTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	private Populator populator;

	@Mock
	private CepRuleMongoRepository cepRuleRepository;

	@Mock
	private SiddhiService siddhiService;

	@Mock
	private UserService userService;

	@Spy
	@InjectMocks
	private CepRuleService cepRuleService;

	@Before
	public void setUp() throws Exception {
		populator = new PopulatorBuilder().build();
	}

	@Test(expected = NullPointerException.class)
	public void testSave1() {

		CepRule cepRule = populator.populateBean(CepRule.class, "cepRuleId");
		cepRuleService.save(cepRule);

	}

	@Test(expected = NullPointerException.class)
	public void testSave2() {
		CepRule cepRule = populator.populateBean(CepRule.class, "cepRuleId");
		cepRuleService.save(cepRule);
	}

	@Test(expected = RuntimeException.class)
	public void testSave3() {
		cepRuleService.save(null);
	}

	@Test
	public void testSave_insert() {
		CepRule cepRule = populator.populateBean(CepRule.class, "cepRuleId", "changedDate", "createdDate");
		cepRule.setMessage(populator.populateBeans(MessageItem.class, 1));
		String user = "ZATTA1";
		String cepRuleId = "577d3e9544efa608dfb7c59e";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -5);

		Mockito.when(userService.getAuthenticatedUserName()).thenReturn(user);

		Mockito.when(cepRuleService.now()).thenReturn(calendar.getTime());

		Mockito.when(cepRuleRepository.save(cepRule)).thenAnswer(new Answer<CepRule>() {
			@Override
			public CepRule answer(InvocationOnMock invocation) {
				CepRule cepRule = (CepRule) invocation.getArguments()[0];
				cepRule.setCepRuleId(cepRuleId);
				return cepRule;
			}
		});

		CepRule result = cepRuleService.save(cepRule);
		Assert.assertNotNull(result);

		Assert.assertNotNull(cepRule.getCepRuleId());
		Assert.assertEquals(cepRuleId, result.getCepRuleId());

		Assert.assertEquals(user, result.getCreatedBy());
		Assert.assertNotNull(result.getCreatedDate());
		Assert.assertEquals(calendar.getTime(), result.getCreatedDate());

		Assert.assertEquals(user, result.getChangedBy());
		Assert.assertNotNull(result.getChangedDate());
		Assert.assertEquals(calendar.getTime(), result.getChangedDate());

		Mockito.verify(cepRuleRepository).save(Mockito.any(CepRule.class));
	}

	@Test
	@Ignore// reativa cenário apenas após implementar privilégio de administrador
	public void testSave_update() {
		String createdBy = "ZATTA0";
		Calendar createdDate = Calendar.getInstance();
		createdDate.add(Calendar.MINUTE, -10);

		CepRule cepRule = populator.populateBean(CepRule.class, "createdBy", "createdDate");
		cepRule.setMessage(populator.populateBeans(MessageItem.class, 1));
		cepRule.setCreatedBy(createdBy);
		cepRule.setCreatedDate(createdDate.getTime());

		String user = "ZATTA1";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -5);

		Mockito.when(userService.getAuthenticatedUserName()).thenReturn(user);

		Mockito.when(cepRuleService.now()).thenReturn(calendar.getTime());

		Mockito.when(cepRuleRepository.findOne(Mockito.anyString())).thenReturn(cepRule);

		Mockito.when(cepRuleRepository.save(cepRule)).thenAnswer(new Answer<CepRule>() {
			@Override
			public CepRule answer(InvocationOnMock invocation) {
				return (CepRule) invocation.getArguments()[0];
			}
		});

		CepRule result = cepRuleService.save(cepRule);
		Assert.assertNotNull(result);

		Assert.assertEquals(createdBy, result.getCreatedBy());
		Assert.assertEquals(createdDate.getTime(), result.getCreatedDate());

		Assert.assertEquals(user, result.getChangedBy());
		Assert.assertEquals(calendar.getTime(), result.getChangedDate());

		Mockito.verify(cepRuleRepository).save(Mockito.any(CepRule.class));
	}

	@Test
	public void testPopulateSituation() {
		CepRule cepRule = populator.populateBean(CepRule.class, "createdBy", "createdDate", "situation", "tool");
		cepRule.setTool("hypervisor");
		List<CepRule> list = populator.populateBeans(CepRule.class, 5, "createdBy", "createdDate", "situation");
		for (CepRule item : list) {
			item.setSituation("hypervisor_5");
		}
		Mockito.doReturn(list).when(cepRuleService).findBySituation(Mockito.anyString());

		cepRuleService.populateSituation(cepRule);
		Assert.assertNotNull(cepRule.getSituation());
		Assert.assertEquals("hypervisor_6", cepRule.getSituation());

	}
}
