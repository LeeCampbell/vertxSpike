package com.leecampbell.playerservice;

import io.reactiverse.pgclient.PgClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class PlayerQueryVerticle extends AbstractVerticle {

//    @Override
//    public void start(Future<Void> fut) {
//        vertx.createHttpServer()
//            .requestHandler(r -> {
//                r.response().end("<h1>Hello Lee, from my first " +
//                        "Vert.x 3 application</h1>");
//            })
//            .listen(8080, result -> {
//                if (result.succeeded()) {
//                    fut.complete();
//                } else {
//                    fut.fail(result.cause());
//                }
//            });
//    }

    @Override
    public void start(Future<Void> fut) {
        Router router = Router.router(vertx);
        //TODO: What is this? -LC
        router.route().handler(BodyHandler.create());
        router.get("/").handler(rc -> handleRoot(rc.request()));
        router.get("/players").handler(this::handleListPlayers);
        router.get("/players/:playerId").handler(this::handleGetPlayer);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }

    private void handleListPlayers(RoutingContext routingContext) {
        routingContext.request().response().end("Lee");
    }

    private void handleGetPlayer(RoutingContext routingContext) {
        String playerId = routingContext.request().getParam("playerId");
        HttpServerResponse response = routingContext.response();
        if(playerId==null){
            sendError(400, response);
        }else {

        }
    }
    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    private static void handleRoot(HttpServerRequest request) {
        request.response()
                .end("<h1>Hello from my first " +
                        "Vert.x 3 application. Mod by Lee</h1>");
    }
}