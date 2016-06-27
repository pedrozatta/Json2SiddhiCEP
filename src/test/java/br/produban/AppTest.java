package br.produban;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
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
            context.assertEquals(response.headers().get("content-type"), "text/html");
            response.bodyHandler(body -> {
                context.assertTrue(body.toString().contains("<title>Queries Available</title>"));
                async.complete();
            });
        });
    }
}
