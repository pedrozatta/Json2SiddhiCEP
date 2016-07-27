package br.produban.bdm.ceprule;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.produban.bdm.ceprule.Json2SiddhiCepApplication;
import br.produban.bdm.ceprule.model.CepRule;
import br.produban.bdm.ceprule.repository.CepRuleRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Json2SiddhiCepApplication.class)
@WebAppConfiguration
@Ignore
public class CepRuleControllerIT {

	@Rule
	public final RestDocumentation restDocumentation = new RestDocumentation("build/generated-snippets");

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private CepRuleRepository ruleRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	private RestDocumentationResultHandler document;

	@Before
	public void setUp() {
		this.document = document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation)).alwaysDo(this.document).build();
	}

	@Test
	public void listRules() throws Exception {

		List<Map<String, String>> ruleFilters = new ArrayList<Map<String, String>>();

		Map<String, String> filter1 = new HashMap<String, String>();
		filter1.put("field", "platform");
		filter1.put("operator", "==");
		filter1.put("value", "Wintel");
		filter1.put("condition", "And");

		Collections.addAll(ruleFilters, filter1);

		List<Map<String, String>> ruleFilters2 = new ArrayList<Map<String, String>>();

		Map<String, String> filter2 = new HashMap<String, String>();
		filter2.put("field", "metric");
		filter2.put("operator", "!=");
		filter2.put("value", "system.dsk.utilization");
		filter2.put("condition", "FIM");

		Collections.addAll(ruleFilters2, filter2);

		createSampleCepRule("xb182509135201689", "xb182509", "xb182509", "openbus_br_zabbix_v2", ruleFilters);
		createSampleCepRule("xb182509135201619", "xb182753", "xb182753", "openbus_br_zabbix_v3", ruleFilters2);

		this.document.snippets(responseFields(fieldWithPath("[].id").description("The id of rule"),
				fieldWithPath("[].cepRuleId").description("The id of rule"),
				fieldWithPath("[].createdBy").description("The user that create a rule"),
				fieldWithPath("[].changedBy").description("The user that update a rule"),
				fieldWithPath("[].tool").description("The tool of rule"),
				fieldWithPath("[].filters").description("The filter of rule")));

		this.mockMvc.perform(get("/ceprule").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void getRule() throws Exception {

		List<Map<String, String>> ruleFilters = new ArrayList<Map<String, String>>();

		Map<String, String> filter1 = new HashMap<String, String>();
		filter1.put("field", "platform");
		filter1.put("operator", "==");
		filter1.put("value", "Wintel");
		filter1.put("condition", "And");

		Collections.addAll(ruleFilters, filter1);

		CepRule sampleRule = createSampleCepRule("xb182509135201620", "xb182754", "xb182754", "openbus_br_zabbix_v4",
				ruleFilters);

		this.document.snippets(responseFields(fieldWithPath("id").description("The id of rule"),
				fieldWithPath("cepRuleId").description("The id of rule"),
				fieldWithPath("createdBy").description("The user that create a rule"),
				fieldWithPath("changedBy").description("The user that update a rule"),
				fieldWithPath("tool").description("The tool of rule"),
				fieldWithPath("filters").description("The filters of rule")));

		this.mockMvc.perform(get("/ceprule/" + sampleRule.getCepRuleId()).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	public void createCepRule() throws Exception {

		List<Map<String, String>> ruleFilters = new ArrayList<Map<String, String>>();

		Map<String, String> filter1 = new HashMap<String, String>();
		filter1.put("field", "platform");
		filter1.put("operator", "==");
		filter1.put("value", "Wintel");
		filter1.put("condition", "And");

		Collections.addAll(ruleFilters, filter1);

		Map<String, String> newCepRule = new HashMap<>();
		newCepRule.put("cepRuleId", "xb182509135201620");
		newCepRule.put("createdBy", "xb182754");
		newCepRule.put("changedBy", "xb182754");
		newCepRule.put("tool", "openbus_br_zabbix_v4");
		newCepRule.put("filters", ruleFilters.toString());

		ConstrainedFields fields = new ConstrainedFields(CepRule.class);

		this.document.snippets(requestFields(fields.withPath("cepRuleId").description("The id of rule"),
				fields.withPath("createdBy").description("The user that create a rule"),
				fields.withPath("changedBy").description("The user that update a rule"),
				fields.withPath("tool").description("The tool of rule"),
				fields.withPath("filters").description("The filters of rule")));

		this.mockMvc.perform(post("/ceprule").contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(newCepRule))).andExpect(status().isCreated());
	}

	private static class ConstrainedFields {

		private final ConstraintDescriptions constraintDescriptions;

		ConstrainedFields(Class<?> input) {
			this.constraintDescriptions = new ConstraintDescriptions(input);
		}

		private FieldDescriptor withPath(String path) {
			return fieldWithPath(path).attributes(key("constraints").value(StringUtils
					.collectionToDelimitedString(this.constraintDescriptions.descriptionsForProperty(path), ". ")));
		}
	}

	@Test
	public void updateCepRule() throws Exception {

		List<Map<String, String>> ruleFilters = new ArrayList<Map<String, String>>();

		Map<String, String> filter1 = new HashMap<String, String>();
		filter1.put("field", "platform");
		filter1.put("operator", "==");
		filter1.put("value", "Wintel");
		filter1.put("condition", "And");

		Collections.addAll(ruleFilters, filter1);

		CepRule originalCepRule = createSampleCepRule("xb182509135201620", "xb182754", "xb182754",
				"openbus_br_zabbix_v4", ruleFilters);

		List<Map<String, String>> ruleFilters2 = new ArrayList<Map<String, String>>();

		Map<String, String> filter2 = new HashMap<String, String>();
		filter2.put("field", "metric");
		filter2.put("operator", "!=");
		filter2.put("value", "system.dsk.utilization");
		filter2.put("condition", "FIM");

		Collections.addAll(ruleFilters2, filter2);

		Map<String, String> updatedCepRule = new HashMap<>();
		updatedCepRule.put("cepRuleId", "xb182509135201621");
		updatedCepRule.put("createdBy", "xb182755");
		updatedCepRule.put("changedBy", "xb182755");
		updatedCepRule.put("tool", "openbus_br_zabbix_v5");
		updatedCepRule.put("filters", ruleFilters2.toString());

		ConstrainedFields fields = new ConstrainedFields(CepRule.class);

		this.document.snippets(requestFields(fields.withPath("cepRuleId").description("The id of rule"),
				fields.withPath("createdBy").description("The user that create a rule"),
				fields.withPath("changedBy").description("The user that update a rule"),
				fields.withPath("tool").description("The tool of rule"),
				fields.withPath("filters").description("The filters of rule")));

		this.mockMvc
				.perform(put("/ceprule/" + originalCepRule.getCepRuleId()).contentType(MediaType.APPLICATION_JSON)
						.content(this.objectMapper.writeValueAsString(updatedCepRule)))
				.andExpect(status().isNoContent());
	}

	private CepRule createSampleCepRule(String cepRuleId, String createdBy, String changedBy, String tool,
			List<Map<String, String>> filters) {
		return ruleRepository.save(new CepRule(cepRuleId, createdBy, changedBy, tool, null));
	}

}
