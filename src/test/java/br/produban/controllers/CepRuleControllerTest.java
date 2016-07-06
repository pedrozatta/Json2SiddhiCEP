package br.produban.controllers;

import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
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
import br.produban.models.CepRuleFilter;
import br.produban.repositories.CepRuleMongoRepository;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;

public class CepRuleControllerTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	private Populator populator;

	@Mock
	private CepRuleMongoRepository cepRuleRepository;

	@Spy
	@InjectMocks
	private CepRuleController cepRuleController;

	@Before
	public void setUp() throws Exception {
		populator = new PopulatorBuilder().build();
	}

	@Test
	public void testListCepRules1() {
		Iterable<CepRule> value = cepRuleController.listCepRules();
		Assert.assertNotNull(value);
	}

	@Test
	public void testListCepRules2() {
		List<CepRule> list = populator.populateBeans(CepRule.class, 5);
		Mockito.when(cepRuleRepository.findAll()).thenReturn(list);

		Iterable<CepRule> value = cepRuleController.listCepRules();
		Assert.assertNotNull(value);
		Assert.assertEquals(list, value);

		Mockito.verify(cepRuleRepository, Mockito.only()).findAll();
	}

	@Test
	public void testGetCepRule1() {
		CepRule cepRule = cepRuleController.getCepRule("ID-1");
		Assert.assertNull(cepRule);
	}

	@Test
	public void testGetCepRule2() {
		CepRule cepRule = populator.populateBean(CepRule.class);
		Mockito.when(cepRuleRepository.findOne("ID-2")).thenReturn(cepRule);

		CepRule value = cepRuleController.getCepRule("ID-1");
		Assert.assertNull(value);
	}

	@Test
	public void testGetCepRule3() {
		CepRule cepRule = populator.populateBean(CepRule.class);
		cepRule.setFilters(populator.populateBeans(CepRuleFilter.class, 10));
		Mockito.when(cepRuleRepository.findOne("ID-2")).thenReturn(cepRule);

		CepRule value = cepRuleController.getCepRule("ID-2");
		Assert.assertNotNull(value);
		Assert.assertEquals(cepRule, value);
	}

	@Test
	public void testCreateCepRule() {
		CepRule cepRule = populator.populateBean(CepRule.class);
		Mockito.when(cepRuleRepository.save(cepRule)).thenReturn(cepRule);

		CepRule value = cepRuleController.createCepRule(cepRule);
		Assert.assertNotNull(value);
		Assert.assertEquals(cepRule.getCepRuleId(), value.getCepRuleId());
		Assert.assertEquals(cepRule, value);

		Mockito.verify(cepRuleRepository, Mockito.only()).save(Mockito.any(CepRule.class));

	}

	@Test
	public void testUpdateCepRule() {
		CepRule cepRule = populator.populateBean(CepRule.class);

		Mockito.when(cepRuleRepository.save(cepRule)).thenReturn(cepRule);
		Mockito.when(cepRuleRepository.findOne(Mockito.anyString())).thenReturn(new CepRule());

		cepRuleController.updateCepRule(cepRule.getCepRuleId(), cepRule);

		Mockito.verify(cepRuleRepository).save(Mockito.any(CepRule.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSave1() {

		CepRule cepRule = populator.populateBean(CepRule.class, "cepRuleId");
		cepRuleController.save(null, cepRule);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testSave2() {

		CepRule cepRule = populator.populateBean(CepRule.class, "cepRuleId");
		cepRuleController.save("", cepRule);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testSave3() {

		cepRuleController.save("Zatta1", null);

	}

	@Test
	public void testSave_insert() {
		CepRule cepRule = populator.populateBean(CepRule.class, "cepRuleId", "changedDate", "createdDate");
		String user = "ZATTA1";
		String cepRuleId = "577d3e9544efa608dfb7c59e";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -5);

		Mockito.when(cepRuleController.now()).thenReturn(calendar.getTime());

		Mockito.when(cepRuleRepository.save(cepRule)).thenAnswer(new Answer<CepRule>() {
			@Override
			public CepRule answer(InvocationOnMock invocation) {
				CepRule cepRule = (CepRule) invocation.getArguments()[0];
				cepRule.setCepRuleId(cepRuleId);
				return cepRule;
			}
		});

		CepRule result = cepRuleController.save(user, cepRule);
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
	public void testSave_update() {
		String createdBy = "ZATTA0";
		Calendar createdDate = Calendar.getInstance();
		createdDate.add(Calendar.MINUTE, -10);

		CepRule cepRule = populator.populateBean(CepRule.class, "createdBy", "createdDate");
		cepRule.setCreatedBy(createdBy);
		cepRule.setCreatedDate(createdDate.getTime());

		String user = "ZATTA1";
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -5);

		Mockito.when(cepRuleController.now()).thenReturn(calendar.getTime());

		Mockito.when(cepRuleRepository.save(cepRule)).thenAnswer(new Answer<CepRule>() {
			@Override
			public CepRule answer(InvocationOnMock invocation) {
				return (CepRule) invocation.getArguments()[0];
			}
		});

		CepRule result = cepRuleController.save(user, cepRule);
		Assert.assertNotNull(result);

		Assert.assertEquals(createdBy, result.getCreatedBy());
		Assert.assertEquals(createdDate.getTime(), result.getCreatedDate());

		Assert.assertEquals(user, result.getChangedBy());
		Assert.assertEquals(calendar.getTime(), result.getChangedDate());

		Mockito.verify(cepRuleRepository).save(Mockito.any(CepRule.class));
	}

}
