package br.produban.bdm.ceprule.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

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

import br.produban.bdm.ceprule.controller.CepRuleController;
import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.ceprule.model.CepRuleItem;
import br.produban.bdm.ceprule.service.CepRuleService;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;

public class CepRuleControllerTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	private Populator populator;

	@Mock
	private CepRuleService cepRuleService;

	@Spy
	@InjectMocks
	private CepRuleController cepRuleController;

	@Before
	public void setUp() throws Exception {
		populator = new PopulatorBuilder().build();
	}

	@Test
	public void testListCepRules() {
		List<CepRule> list = populator.populateBeans(CepRule.class, 5);
		Mockito.when(cepRuleService.findAll()).thenReturn(list);

		Iterable<CepRule> value = cepRuleController.findAll();
		Assert.assertNotNull(value);
		Assert.assertEquals(list, value);

		Mockito.verify(cepRuleService, Mockito.only()).findAll();
	}

	@Test
	public void testFindById1() {
		CepRule cepRule = cepRuleController.findById("ID-1");
		Assert.assertNull(cepRule);
	}

	@Test
	public void testFindById2() {
		CepRule cepRule = populator.populateBean(CepRule.class);
		Mockito.when(cepRuleService.findOne("ID-2")).thenReturn(cepRule);

		CepRule value = cepRuleController.findById("ID-1");
		Assert.assertNull(value);
	}

	@Test
	public void testFindById3() {
		CepRule cepRule = populator.populateBean(CepRule.class);
		cepRule.setChildren(populator.populateBeans(CepRuleItem.class, 10));
		Mockito.when(cepRuleService.findOne("ID-2")).thenReturn(cepRule);

		CepRule value = cepRuleController.findById("ID-2");
		Assert.assertNotNull(value);
		Assert.assertEquals(cepRule, value);
	}

	@Test
	public void testCreateCepRule()
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		CepRule cepRule = populator.populateBean(CepRule.class);
		Mockito.when(cepRuleService.save(Mockito.any(CepRule.class))).thenReturn(cepRule);

		CepRule value = cepRuleController.createCepRule(cepRule, null);
		Assert.assertNotNull(value);
		Assert.assertEquals(cepRule.getCepRuleId(), value.getCepRuleId());
		Assert.assertEquals(cepRule, value);

		Mockito.verify(cepRuleService, Mockito.only()).save(Mockito.any(CepRule.class));
	}

	@Test
	public void testUpdateCepRule() {
		CepRule cepRule = populator.populateBean(CepRule.class);

		Mockito.when(cepRuleService.save(cepRule)).thenReturn(cepRule);
		Mockito.when(cepRuleService.findOne(Mockito.anyString())).thenReturn(new CepRule());

		cepRuleController.updateCepRule(cepRule.getCepRuleId(), cepRule);

		Mockito.verify(cepRuleService).save(cepRule);
	}

}
