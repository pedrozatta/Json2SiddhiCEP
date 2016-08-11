package br.produban.bdm.ceprule.service;

import java.io.File;
import java.io.IOException;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.ceprule.model.ExecutionPlan;
import br.produban.bdm.ceprule.model.Tool;
import br.produban.bdm.ceprule.ws.soap.EventProcessorAdminServiceClient;
import io.github.benas.jpopulator.api.Populator;
import io.github.benas.jpopulator.impl.PopulatorBuilder;

public class SiddhiServiceTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	private Populator populator;

	@Mock
	private EventProcessorAdminServiceClient eventProcessorAdminServiceClient;

	@Mock
	private ToolService toolService;

	private ObjectMapper mapper;

	@Spy
	@InjectMocks
	private SiddhiService siddhiService;

	@Before
	public void setUp() throws Exception {
		populator = new PopulatorBuilder().build();
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		siddhiService.templatesPath = "src/main/resources/templates/";
	}

	@Test
	public void testGenerateExecutionPlan() throws JsonParseException, JsonMappingException, IOException {

		Mockito.when(toolService.findById(Mockito.anyString())).thenAnswer(new Answer<Tool>() {
			@Override
			public Tool answer(InvocationOnMock invocation) {
				Tool value = populator.populateBean(Tool.class);
				value.setNickName("hypervisor");
				return value;
			}
		});

		Mockito.when(eventProcessorAdminServiceClient.validateExecutionPlan(Mockito.anyString()))
				.thenAnswer(new Answer<ExecutionPlan>() {
					@Override
					public ExecutionPlan answer(InvocationOnMock invocation) {
						ExecutionPlan executionPlan = new ExecutionPlan();
						executionPlan.setPlan((String) invocation.getArguments()[0]);
						return executionPlan;
					}
				});

		CepRule cepRule = mapper.readValue(new File("src/test/resources/request4.json"), CepRule.class);
		Assert.assertNotNull(cepRule);

		ExecutionPlan result = siddhiService.generateExecutionPlan(cepRule);

		Assert.assertTrue(result.getPlan().indexOf("\"hypervisor_5\" as situation") > 0);

	}

	@Test
	@SuppressWarnings("deprecation")
	public void testGenerateExecutionPlanRequest1() throws JsonParseException, JsonMappingException, IOException {

		CepRule cepRule = mapper.readValue(new File("src/test/resources/request1.json"), CepRule.class);
		Assert.assertNotNull(cepRule);

		StringBuilder sb = new StringBuilder();
		siddhiService.generateRule(sb, cepRule);

		Assert.assertTrue(sb.indexOf("FDC") > 0);
		Assert.assertTrue(sb.indexOf("metric") > 0);
		Assert.assertTrue(sb.indexOf("value") > 0);
		Assert.assertTrue(sb.indexOf("AND") > 0);

	}

	@Test
	@SuppressWarnings("deprecation")
	public void testGenerateExecutionPlanRequest2() throws JsonParseException, JsonMappingException, IOException {

		CepRule cepRule = mapper.readValue(new File("src/test/resources/request2.json"), CepRule.class);
		Assert.assertNotNull(cepRule);

		StringBuilder sb = new StringBuilder();
		siddhiService.generateRule(sb, cepRule);

		Assert.assertTrue(sb.indexOf("FDC") > 0);
		Assert.assertTrue(sb.indexOf("metric") > 0);
		Assert.assertTrue(sb.indexOf("value") > 0);
		Assert.assertTrue(sb.indexOf("AND") > 0);

	}

	@Test
	@SuppressWarnings("deprecation")
	public void testGenerateExecutionPlanRequest3() throws JsonParseException, JsonMappingException, IOException {

		CepRule cepRule = mapper.readValue(new File("src/test/resources/request3.json"), CepRule.class);
		Assert.assertNotNull(cepRule);

		StringBuilder sb = new StringBuilder();
		siddhiService.generateRule(sb, cepRule);

		Assert.assertTrue(sb.indexOf("'Linux'") > 0);
		Assert.assertTrue(sb.indexOf("metric") > 0);
		Assert.assertTrue(sb.indexOf("value") > 0);
		Assert.assertTrue(sb.indexOf("AND") > 0);

	}

	@Test(expected = RuntimeException.class)
	public void testGenerateMessageNull() {
		siddhiService.generateMessage(null);
	}

	@Test(expected = RuntimeException.class)
	public void testGenerateMessageEmpty() {
		siddhiService.generateMessage("");
	}

	@Test
	public void testGenerateMessageNoVariable() {
		String message = "O consumo de Disco ou FS está acima do threshold estipulado em 80%.";
		String result = siddhiService.generateMessage(message);
		Assert.assertTrue(result.indexOf('\"') == 0);
		Assert.assertTrue(result.lastIndexOf('\"') == result.length() - 1);
		Assert.assertTrue(result.contains(message));
	}

	@Test
	public void testGenerateMessageOneVariable() {
		String message = "O consumo de Disco ou FS está acima do threshold estipulado em 80%. Valor atual {value}.";
		String result = siddhiService.generateMessage(message);
		Assert.assertTrue(result.indexOf('\"') == 0);
		Assert.assertTrue(result.lastIndexOf('\"') == result.length() - 1);
		Assert.assertTrue(result.contains("\", value, \""));
	}

	@Test
	public void testGenerateMessageTwoVariable() {
		String message = "O consumo de Disco ou FS está acima do threshold estipulado em 80%. Valor atual {value}  Disco ou FS {fileSystem}.";
		String result = siddhiService.generateMessage(message);
		Assert.assertTrue(result.indexOf('\"') == 0);
		Assert.assertTrue(result.lastIndexOf('\"') == result.length() - 1);
		Assert.assertTrue(result.contains("\", value, \""));
		Assert.assertTrue(result.contains("\", fileSystem, \""));
	}

}
