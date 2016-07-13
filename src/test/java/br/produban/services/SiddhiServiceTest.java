package br.produban.services;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.produban.models.CepRule;

public class SiddhiServiceTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	private ObjectMapper mapper;

	@Spy
	@InjectMocks
	private SiddhiService siddhiService;

	@Before
	public void setUp() throws Exception {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	@Test
	@SuppressWarnings("deprecation")
	public void testGenerateSiddhiRequest1() throws JsonParseException, JsonMappingException, IOException {

		CepRule cepRule = mapper.readValue(new File("src/test/resources/request1.json"), CepRule.class);
		Assert.assertNotNull(cepRule);

		StringBuilder sb = new StringBuilder();
		siddhiService.generateRule(sb, cepRule);
		System.out.println(sb);

		Assert.assertTrue(sb.indexOf("FDC") > 0);
		Assert.assertTrue(sb.indexOf("metric") > 0);
		Assert.assertTrue(sb.indexOf("value") > 0);
		Assert.assertTrue(sb.indexOf("AND") > 0);

	}

	@Test
	@SuppressWarnings("deprecation")
	public void testGenerateSiddhiRequest2() throws JsonParseException, JsonMappingException, IOException {

		CepRule cepRule = mapper.readValue(new File("src/test/resources/request2.json"), CepRule.class);
		Assert.assertNotNull(cepRule);

		StringBuilder sb = new StringBuilder();
		siddhiService.generateRule(sb, cepRule);
		System.out.println(sb);

		Assert.assertTrue(sb.indexOf("FDC") > 0);
		Assert.assertTrue(sb.indexOf("metric") > 0);
		Assert.assertTrue(sb.indexOf("value") > 0);
		Assert.assertTrue(sb.indexOf("AND") > 0);

	}

	@Test
	@SuppressWarnings("deprecation")
	public void testGenerateSiddhiRequest3() throws JsonParseException, JsonMappingException, IOException {

		CepRule cepRule = mapper.readValue(new File("src/test/resources/request3.json"), CepRule.class);
		Assert.assertNotNull(cepRule);

		StringBuilder sb = new StringBuilder();
		siddhiService.generateRule(sb, cepRule);
		System.out.println(sb);

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
		Assert.assertTrue(result.lastIndexOf('\"') == result.length()-1);
		Assert.assertTrue(result.contains(message));
	}

	@Test
	public void testGenerateMessageOneVariable() {
		String message = "O consumo de Disco ou FS está acima do threshold estipulado em 80%. Valor atual {value}.";
		String result = siddhiService.generateMessage(message);
		Assert.assertTrue(result.indexOf('\"') == 0);
		Assert.assertTrue(result.lastIndexOf('\"') == result.length()-1);
		Assert.assertTrue(result.contains("\", value, \""));
	}

	@Test
	public void testGenerateMessageTwoVariable() {
		String message = "O consumo de Disco ou FS está acima do threshold estipulado em 80%. Valor atual {value}  Disco ou FS {fileSystem}.";
		String result = siddhiService.generateMessage(message);
		Assert.assertTrue(result.indexOf('\"') == 0);
		Assert.assertTrue(result.lastIndexOf('\"') == result.length()-1);
		Assert.assertTrue(result.contains("\", value, \""));
		Assert.assertTrue(result.contains("\", fileSystem, \""));
	}

}
