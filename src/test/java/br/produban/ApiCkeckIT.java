package br.produban;

import br.produban.Entities.Rule;
import com.jayway.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by bera on 27/06/16.
 */
public class ApiCkeckIT {

    @BeforeClass
    public static void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = Integer.getInteger("http.port", 8081);
    }

    @AfterClass
    public static void unconfigureRestAssured() {
        RestAssured.reset();
    }


    @Test
    public void checkThatWeCanRetrieveIndividualRule() {
        // Get the list of rules, ensure it's a success and extract the first id.
        //final String json = get("/api/rules").then()
        //        .extract().response().getBody().prettyPrint();

        final int id = get("/api/rules").then()
                .assertThat()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("find { it.tool=='openbus_br_zabbix_v2' }.id");
        // Now get the individual resource and check the content
        get("/api/rules/" + id).then()
                .assertThat()
                .statusCode(200)
                .body("tool", equalTo("openbus_br_zabbix_v2"))
                .body("createdByUser", equalTo("xb182509"))
                .body("id", equalTo(id));

    }


    @Test
    public void checkWeCanAddAndDeleteARule() {
        // Create a new rule and retrieve the result (as a Rule instance).
        Rule rule = given()
                .body("{\"tool\":\"openbus_br_zabbix_v4\", \"createdByUser\":\"xb181753\"}").request().post("/api/rules")
                .thenReturn().as(Rule.class);
        assertThat(rule.getTool()).isEqualToIgnoringCase("openbus_br_zabbix_v4");
        assertThat(rule.getCreatedByUser()).isEqualToIgnoringCase("xb181753");
        assertThat(rule.getId()).isNotZero();
        // Check that it has created an individual resource, and check the content.
        get("/api/rules/" + rule.getId()).then()
                .assertThat()
                .statusCode(200)
                .body("tool", equalTo("openbus_br_zabbix_v4"))
                .body("createdByUser", equalTo("xb181753"))
                .body("id", equalTo(rule.getId()));
        // Delete the rule
        delete("/api/rules/" + rule.getId()).then().assertThat().statusCode(204);
        // Check that the resource is not available anymore
        get("/api/rules/" + rule.getId()).then()
                .assertThat()
                .statusCode(404);
    }
}
