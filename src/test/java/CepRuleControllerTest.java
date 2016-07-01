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
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
                        //fieldWithPath("[].id").description("The rules' ID"),
                        fieldWithPath("[].cepRuleId").description("The rule id"),
                        fieldWithPath("[].createdBy").description("The user that create a rule"),
                        fieldWithPath("[].changedBy").description("The user that update a rule"),
                        fieldWithPath("[].tool").description("The tool of the rule"),
                        fieldWithPath("[].filters").description("The rule filter")
                )
        );

        this.mockMvc.perform(
                get("/ceprule").accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    private CepRule createSampleCepRule(String cepRuleId, String createdBy, String changedBy,
                                        String tool, String filters) {
        return ruleRepository.save(new CepRule(cepRuleId, createdBy, changedBy,
                tool, filters));
    }

}

