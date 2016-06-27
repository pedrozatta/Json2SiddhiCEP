package br.produban;

import br.produban.Entities.Rule;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;


/**
 * Unit test for simple AppEntry.
 */
@RunWith(VertxUnitRunner.class)
public class AppTest 

{
    private Vertx vertx;
    private int port = 0;

    @Before
    public void setUp(TestContext context) {

        vertx = Vertx.vertx();

        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );
        vertx.deployVerticle(AppEntry.class.getName(), options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {

        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testDummyAppVertXIsWorking(TestContext context) {

        final Async async = context.async();
        vertx.createHttpClient().getNow(port, "localhost", "/",
                response -> {
                    response.handler(body -> {
                        context.assertTrue(body.toString().contains("Working"));
                        async.complete();
                    });
                });
    }

    @Test
    public void checkThatIndexPageIsServed(TestContext context) {
        Async async = context.async();
        vertx.createHttpClient().getNow(port, "localhost", "/assets/index.html", response -> {
            context.assertEquals(response.statusCode(),200);
            context.assertEquals(response.headers().get("content-type"), "text/html;charset=UTF-8");
            response.bodyHandler(body -> {
                context.assertTrue(body.toString().contains("<title>Queries Available</title>"));
                async.complete();
            });
        });
    }

    @Test
    public void checkThatCanAddRule(TestContext context) {
        Async async = context.async();
        final String json = Json.encodePrettily(new Rule(
                 "xb181753088923019548",
                 "xb181753",
                 "xb181753: (13/5/2016)-(11:17:17)",
                 "openbus_br_zabbix_v4",
                 null
        ));
        final String length = Integer.toString(json.length());
        vertx.createHttpClient().post(port,"localhost", "/api/rules")
                .putHeader("content-type", "application/json")
                .putHeader("content-length", length)
                .handler(response -> {
                    context.assertEquals(response.statusCode(), 201);
                    context.assertTrue(response.headers().get("content-type").contains("application/json"));
                    response.bodyHandler(body -> {
                        final Rule rule = Json.decodeValue(body.toString(), Rule.class);
                        context.assertEquals(rule.getRuleid(), "xb181753088923019548");
                        context.assertEquals(rule.getCreated_by_user(), "xb181753");
                        context.assertEquals(rule.getTool(), "openbus_br_zabbix_v4");
                        context.assertNotNull(rule.getId());
                        async.complete();
                    });
                })
                .write(json)
                .end();
    }
}
