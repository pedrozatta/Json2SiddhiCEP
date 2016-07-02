import br.produban.Json2SiddhiCepApplication;
import br.produban.models.CepRule;
import br.produban.repositories.CepRuleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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

import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Json2SiddhiCepApplication.class)
@WebAppConfiguration
public class CepRuleControllerTest {
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
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.document)
                .build();
    }

    @Test
    public void listRules() throws Exception {
        createSampleCepRule("xb182509135201689", "xb182509", "xb182509", "openbus_br_zabbix_v2", "all");
        createSampleCepRule("xb182509135201619", "xb182753", "xb182753", "openbus_br_zabbix_v3", "one");


        this.document.snippets(
                responseFields(

                        fieldWithPath("[].cepRuleId").description("The id of rule"),
                        fieldWithPath("[].createdBy").description("The user that create a rule"),
                        fieldWithPath("[].changedBy").description("The user that update a rule"),
                        fieldWithPath("[].tool").description("The tool of rule"),
                        fieldWithPath("[].filters").description("The filter of rule")
                )
        );

        this.mockMvc.perform(
                get("/ceprule").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void getRule() throws Exception {
        CepRule sampleRule =  createSampleCepRule("xb182509135201620",
                "xb182754", "xb182754", "openbus_br_zabbix_v4", "anyone");


        this.document.snippets(
                responseFields(
                        fieldWithPath("cepRuleId").description("The id of rule"),
                        fieldWithPath("createdBy").description("The user that create a rule"),
                        fieldWithPath("changedBy").description("The user that update a rule"),
                        fieldWithPath("tool").description("The tool of rule"),
                        fieldWithPath("filters").description("The filters of rule")
                )
        );

        this.mockMvc.perform(
                get("/ceprule/" + sampleRule.getCepRuleId()).accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    public void createCepRule() throws Exception {
        Map<String, String> newCepRule = new HashMap<>();
        newCepRule.put("cepRuleId", "xb182509135201620");
        newCepRule.put("createdBy", "xb182754");
        newCepRule.put("changedBy", "xb182754");
        newCepRule.put("tool", "openbus_br_zabbix_v4");
        newCepRule.put("filters", "openbus_br_zabbix_v4");

        ConstrainedFields fields = new ConstrainedFields(CepRule.class);

        this.document.snippets(
                requestFields(
                        fields.withPath("cepRuleId").description("The id of rule"),
                        fields.withPath("createdBy").description("The user that create a rule"),
                        fields.withPath("changedBy").description("The user that update a rule"),
                        fields.withPath("tool").description("The tool of rule"),
                        fields.withPath("filters").description("The filters of rule")
                )
        );

        this.mockMvc.perform(
                post("/ceprule").contentType(MediaType.APPLICATION_JSON).content(
                        this.objectMapper.writeValueAsString(newCepRule)
                )
        ).andExpect(status().isCreated());
    }


    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }

    private CepRule createSampleCepRule(String cepRuleId, String createdBy, String changedBy,
                                        String tool, String filters) {
        return ruleRepository.save(new CepRule(cepRuleId, createdBy, changedBy,
                tool, filters));
    }

}

