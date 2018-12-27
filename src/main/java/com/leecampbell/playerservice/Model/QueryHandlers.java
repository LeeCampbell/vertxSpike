package com.leecampbell.playerservice.Model;

import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgIterator;
import io.reactiverse.pgclient.PgRowSet;
import io.reactiverse.pgclient.Tuple;
import io.vertx.core.Future;

import java.util.Optional;

public class QueryHandlers {
    private static final String SELECT_PLAYER = "SELECT screen_name from player_service.player where player_id=$1";

    private final PgClient pgClient;

    public QueryHandlers(PgClient pgClient){

        this.pgClient = pgClient;
    }

    public io.vertx.core.Future<Optional<PlayerState>> getPlayer(int playerId) {
        io.vertx.core.Future<PgRowSet> result = io.vertx.core.Future.future();
        pgClient.preparedQuery(SELECT_PLAYER, Tuple.of(playerId), res -> result.handle(res));

        return result.compose(rows -> {
            PgIterator resultSet = rows.iterator();
            if (!resultSet.hasNext()) {
                return io.vertx.core.Future.succeededFuture(Optional.empty());
            }
            Tuple row = resultSet.next();
            PlayerState playerState = new PlayerState();
            playerState.playerId = playerId;
            playerState.screenName = row.getString(0);
            return Future.succeededFuture(Optional.of(playerState));
        });
    }
}
