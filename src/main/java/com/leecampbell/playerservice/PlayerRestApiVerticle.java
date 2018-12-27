package com.leecampbell.playerservice;

import com.leecampbell.playerservice.Model.*;
import io.reactiverse.pgclient.PgClient;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Optional;

public class PlayerRestApiVerticle extends AbstractVerticle {
    private CommandHandlers commandHandlers;
    private QueryHandlers queryHandlers;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start(Future<Void> fut) {
        PgClient pgClient = VertxPgClientFactory.create(vertx, config());
        queryHandlers = new QueryHandlers(pgClient);
        commandHandlers = new CommandHandlers(new PostgresPlayerRepository(pgClient));

        Router router = Router.router(vertx);
        router.get("/players/:playerId").handler(this::handleGetPlayer);
        router.post("/players/:playerId").handler(this::handleAddPlayer);
        router.delete("/players/:playerId").handler(this::handleDeletePlayer);
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

    private void handleGetPlayer(RoutingContext routingContext) {
        logger.info("handleGetPlayer");
        String playerId = routingContext.request().getParam("playerId");
        logger.debug(playerId);
        HttpServerResponse response = routingContext.response();
        if(playerId==null){
            sendError(400, response);
        }else {
            Integer id = Integer.parseInt(playerId);
            Future<Optional<PlayerState>> getPlayer = queryHandlers.getPlayer(id);
            getPlayer.setHandler(ar->{
                if(ar.failed()){
                    logger.warn(ar.cause());
                    sendError(500, response);
                }else if(!ar.result().isPresent()){
                    sendError(404, response);
                }else {
                    JsonObject json = JsonObject.mapFrom(ar.result().get());
                    response.setStatusCode(200)
                            .end(json.toBuffer());
                }
            });
        }
    }

    private void handleAddPlayer(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        String playerId = request.getParam("playerId");
        HttpServerResponse response = routingContext.response();

        if (playerId == null) {
            sendError(400, response);
        } else {
            logger.info("PlayerId:" + playerId);

            Future<RegisterPlayerCommand> getCommand = Future.future();
            request.bodyHandler(totalBuffer -> {
                JsonObject body = totalBuffer.toJsonObject();
                RegisterPlayerCommand registerCommand = createRegisterCommand(playerId, body);
                getCommand.complete(registerCommand);
            });

            Future<Void> registerPlayer = getCommand.compose(registerPlayerCommand -> commandHandlers.handle(registerPlayerCommand));
            registerPlayer.setHandler(ar -> {
                if (!handleFailure(ar, response)) {
                    response.setStatusCode(201).end();
                }
            });
        }
    }

    private RegisterPlayerCommand createRegisterCommand(String playerId, JsonObject json) {
        String screenName = json.getString("screenName");
        Integer id = Integer.parseInt(playerId);
        return new RegisterPlayerCommand(id, screenName);
    }

    private void handleDeletePlayer(RoutingContext routingContext) {
        String playerId = routingContext.request().getParam("playerId");
        HttpServerResponse response = routingContext.response();
        if (playerId == null) {
            sendError(400, response);
        } else {

        }
    }

    private boolean handleFailure(AsyncResult ar, HttpServerResponse response) {
        if (ar.failed()) {
            logger.warn("Handling failure", ar.cause());
            //TODO: 409-conflict if exists
            //TODO: 401-Unauthorized if not authenticated
            //TODO: 403-Forbidden if not authorized
            if (ar.cause() instanceof DomainInvarianceException) {
                sendError(400, response);
            } else {
                sendError(500, response);
            }
            return true;
        }
        return false;
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}