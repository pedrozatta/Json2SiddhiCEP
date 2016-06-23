package br.produban;

import br.produban.Entities.Rule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Hello world!
 */
public class AppEntry extends AbstractVerticle {
    @Override
    public void start(Future<Void> fut) {


        createData();

        // Create a router object.
        Router router = Router.router(vertx);

        // Bind "/" to our hello message - so we are still compatible.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Working</h1>");
        });

        router.route("/assets/*").handler(StaticHandler.create("assets"));

        router.get("/api/rules").handler(this::getAllRules);
        router.route("/api/rules*").handler(BodyHandler.create());
        router.post("/api/rules").handler(this::addRule);
        router.get("/api/rules/:id").handler(this::getRule);
        router.put("/api/rules/:id").handler(this::updateRule);
        router.delete("/api/rules/:id").handler(this::deleteRule);

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    private void createData() {
        Rule regra1 = new Rule(
                "xb1825091352016111717889",
                "xb182509",
                "xb182509: (13/5/2016)-(11:17:17)",
                "openbus_br_zabbix_v2",
                null);
        rules.put(regra1.getId(), regra1);
        Rule regra2 = new Rule(
                "xb1817531352016111717889",
                "xb181753",
                "xb181753: (13/5/2016)-(11:17:17)",
                "openbus_br_vhyper_v1",
                null);
        rules.put(regra2.getId(), regra2);
    }

    private void addRule(RoutingContext routingContext) {
        // Read the request's content and create an instance of Rule.
        final Rule Rule = Json.decodeValue(routingContext.getBodyAsString(),
                Rule.class);
        // Add it to the backend map
        rules.put(Rule.getId(), Rule);

        // Return the created Rule as JSON
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(Rule));
    }

    private void getRule(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Rule Rule = rules.get(idAsInteger);
            if (Rule == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(Rule));
            }
        }
    }

    private void updateRule(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Rule Rule = rules.get(idAsInteger);
            if (Rule == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                Rule.setTool(json.getString("tool"));
                Rule.setCreated_by_user(json.getString("created_by_user"));
                Rule.setEdited_by_user(json.getString("edited_by_user"));
                Rule.setRuleid(json.getString("ruleid"));

                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(Rule));
            }
        }
    }

    private void deleteRule(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            rules.remove(idAsInteger);
        }
        routingContext.response().setStatusCode(204).end();
    }

    private void getAllRules(RoutingContext routingContext) {
        // Write the HTTP response
        // The response is in JSON using the utf-8 encoding
        // We returns the list of bottles
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(rules.values()));
    }


    // Store rules
    private Map<Integer, Rule> rules = new LinkedHashMap<>();
}
