package br.produban;

import br.produban.Entities.Rule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.List;
import java.util.stream.Collectors;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Hello world!
 */
public class AppEntry extends AbstractVerticle {

    @Override
    public void start(Future<Void> fut) {

        // Create a JDBC client
        jdbc = JDBCClient.createShared(vertx, config(), "Rules-Catalog");

        startBackend(
                (connection) -> createSomeData(connection,
                        (nothing) -> startWebApp(
                                (http) -> completeStartup(http, fut)
                        ), fut
                ), fut);
    }

    private void startBackend(Handler<AsyncResult<SQLConnection>> next, Future<Void> fut) {
        jdbc.getConnection(ar -> {
            if (ar.failed()) {
                fut.fail(ar.cause());
            } else {
                next.handle(Future.succeededFuture(ar.result()));
            }
        });
    }

    private void startWebApp(Handler<AsyncResult<HttpServer>> next) {
        //createData();

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

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8081),
                        next::handle
                );
    }

    private void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut) {
        if (http.succeeded()) {
            fut.complete();
        } else {
            fut.fail(http.cause());
        }
    }


    @Override
    public void stop() throws Exception {
        // Close the JDBC client.
        jdbc.close();
    }

    private void addRule(RoutingContext routingContext) {
        jdbc.getConnection(ar -> {
            // Read the request's content and create an instance of Rule.
            final Rule rule = Json.decodeValue(routingContext.getBodyAsString(),
                    Rule.class);
            SQLConnection connection = ar.result();
            insert(rule, connection, (r) ->
                    routingContext.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .end(Json.encodePrettily(r.result())));
            connection.close();
        });

    }

    private void getRule(RoutingContext routingContext) {

        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            jdbc.getConnection(ar -> {
                // Read the request's content and create an instance of Rule.
                SQLConnection connection = ar.result();
                select(id, connection, result -> {
                    if (result.succeeded()) {
                        routingContext.response()
                                .setStatusCode(200)
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(Json.encodePrettily(result.result()));
                    } else {
                        routingContext.response()
                                .setStatusCode(404).end();
                    }
                    connection.close();
                });
            });
        }
    }

    private void updateRule(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json = routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            jdbc.getConnection(ar ->
                    update(id, json, ar.result(), (rule) -> {
                        if (rule.failed()) {
                            routingContext.response().setStatusCode(404).end();
                        } else {
                            routingContext.response()
                                    .putHeader("content-type", "application/json; charset=utf-8")
                                    .end(Json.encodePrettily(rule.result()));
                        }
                        ar.result().close();
                    })
            );
        }
    }

    private void deleteRule(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            jdbc.getConnection(ar -> {
                SQLConnection connection = ar.result();
                connection.execute("DELETE FROM Rule WHERE id='" + id + "'",
                        result -> {
                            routingContext.response().setStatusCode(204).end();
                            connection.close();
                        });
            });
        }
    }

    private void getAllRules(RoutingContext routingContext) {
        jdbc.getConnection(ar -> {
            SQLConnection connection = ar.result();
            connection.query("SELECT * FROM Rule", result -> {
                List<Rule> rules = result.result().getRows().stream().map(Rule::new).collect(Collectors.toList());
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(rules));
                connection.close();
            });
        });
    }


    private void createSomeData(AsyncResult<SQLConnection> result,
                                Handler<AsyncResult<Void>> next, Future<Void> fut) {
        if (result.failed()) {
            fut.fail(result.cause());
        } else {
            SQLConnection connection = result.result();
            Rule rule = new Rule();

            connection.execute(
                    "CREATE TABLE IF NOT EXISTS Rule (id INTEGER IDENTITY, ruleid varchar(100), " +
                            "createbyuser varchar(100), updatedbyuser varchar(100), tool varchar(100))",
                    ar -> {
                        if (ar.failed()) {
                            fut.fail(ar.cause());
                            connection.close();
                            return;
                        }
                        connection.query("SELECT * FROM Rule", select -> {
                            if (select.failed()) {
                                fut.fail(ar.cause());
                                connection.close();
                                return;
                            }
                            if (select.result().getNumRows() == 0) {
                                insert(
                                        new Rule("xb1825091352016111717889",
                                                "xb182509",
                                                "xb182509: (13/5/2016)-(11:17:17)",
                                                "openbus_br_zabbix_v2"),
                                        connection,
                                        (v) -> insert(new Rule("xb1817531352016111717889",
                                                        "xb181753",
                                                        "xb181753: (13/5/2016)-(11:17:17)",
                                                        "openbus_br_vhyper_v1"), connection,
                                                (r) -> {
                                                    next.handle(Future.<Void>succeededFuture());
                                                    connection.close();
                                                }));
                            } else {
                                next.handle(Future.<Void>succeededFuture());
                                connection.close();
                            }
                        });
                    });
        }
    }

    private void insert(Rule rule, SQLConnection connection, Handler<AsyncResult<Rule>> next) {
        String sql = "INSERT INTO Rule (ruleid, createbyuser , updatedbyuser , tool ) VALUES ?, ?, ?, ?";

        System.out.println(rule.getRuleId() + " " +
                rule.getCreatedByUser() + " " +
                rule.getEditedByUser() + " " +
                rule.getTool() );
        connection.updateWithParams(sql,
                new JsonArray().add(rule.getRuleId())
                        .add(rule.getCreatedByUser())
                        .add(rule.getEditedByUser())
                        .add(rule.getTool()),
                (ar) -> {
                    if (ar.failed()) {
                        next.handle(Future.failedFuture(ar.cause()));
                        connection.close();
                        return;
                    }
                    UpdateResult result = ar.result();
                    // Build a new Rule instance with the generated id.
                    Rule r = new Rule(result.getKeys().getInteger(0),
                            rule.getRuleId(),
                            rule.getCreatedByUser(),
                            rule.getEditedByUser(),
                            rule.getTool()
                    );
                    next.handle(Future.succeededFuture(r));
                });
    }

    private void select(String id, SQLConnection connection, Handler<AsyncResult<Rule>> resultHandler) {
        connection.queryWithParams("SELECT * FROM Rule WHERE id=?", new JsonArray().add(id), ar -> {
            if (ar.failed()) {
                resultHandler.handle(Future.failedFuture("Rule not found"));
            } else {
                if (ar.result().getNumRows() >= 1) {
                    resultHandler.handle(Future.succeededFuture(new Rule(ar.result()
                            .getRows().get(0))));
                } else {
                    resultHandler.handle(Future.failedFuture("Rule not found"));
                }
            }
        });
    }

    private void update(String id, JsonObject content, SQLConnection connection,
                        Handler<AsyncResult<Rule>> resultHandler) {
        String sql = "UPDATE RULE SET ruleid=?, createdbyuser=?, updatedbyuser=?, tool=? WHERE id=?";
        connection.updateWithParams(sql,
                new JsonArray()
                        .add(content.getString("ruleid"))
                        .add(content.getString("createdbyuser"))
                        .add(content.getString("updatedbyuser"))
                        .add(content.getString("tool"))
                        .add(id),
                update -> {
                    if (update.failed()) {
                        resultHandler.handle(Future.failedFuture("Cannot update the rule"));
                        return;
                    }
                    if (update.result().getUpdated() == 0) {
                        resultHandler.handle(Future.failedFuture("Rule not found"));
                        return;
                    }
                    resultHandler.handle(
                            Future.succeededFuture(
                                    new Rule(
                                        Integer.valueOf(id),
                                        content.getString("ruleid"),
                                        content.getString("createdbyuser"),
                                        content.getString("updatedbyuser"),
                                        content.getString("tool")
                                    )
                            )
                    );
                });
    }

    // Store rules
    private Map<Integer, Rule> rules = new LinkedHashMap<>();
    private JDBCClient jdbc;
}
