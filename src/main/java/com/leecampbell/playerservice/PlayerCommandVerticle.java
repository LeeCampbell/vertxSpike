package com.leecampbell.playerservice;

import com.leecampbell.playerservice.Model.DomainInvarianceException;
import com.leecampbell.playerservice.Model.Player;
import com.leecampbell.playerservice.Model.PlayerRepository;
import com.leecampbell.playerservice.Model.RegisterPlayerCommand;
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

public class PlayerCommandVerticle extends AbstractVerticle {
    private PlayerRepository repo;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void start(Future<Void> fut) {
        PgClient pgClient = VertxPgClientFactory.create(vertx, config());
        repo = new PostgresPlayerRepository(pgClient);
        Router router = Router.router(vertx);
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

            Future<Player> getPlayer = getCommand.compose(registerPlayerCommand -> repo.get(registerPlayerCommand.getPlayerId()));
            Future<Void> savePlayer = getPlayer.compose(player -> {
                player.register(getCommand.result());
                return repo.save(player);
            });
            savePlayer.setHandler(ar -> {
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