package com.leecampbell.playerservice;

import com.leecampbell.playerservice.Model.Player;
import com.leecampbell.playerservice.Model.PlayerRepository;
import com.leecampbell.playerservice.Model.PlayerState;
import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgIterator;
import io.reactiverse.pgclient.PgRowSet;
import io.reactiverse.pgclient.Tuple;
import io.vertx.core.Future;

import java.util.Optional;

public class PostgresPlayerRepository implements PlayerRepository {

    private static final String SELECT_PLAYER = "SELECT screen_name from player_service.player where player_id=$1";
    private static final String INSERT_PLAYER = "INSERT INTO player_service.player (player_id, screen_name) "
            + "VALUES ($1, $2);";

    private final PgClient client;

    public PostgresPlayerRepository(PgClient client) {

        this.client = client;
    }

    @Override
    public Future<Player> get(int id) {
        Future<Optional<PlayerState>> playerState = read(id);
        return playerState.compose(state -> {
            Player player = new Player(id);

            if (state.isPresent()) {
                player.load(state.get());
            }
            return Future.succeededFuture(player);
        });
    }

    @Override
    public Future<Void> save(Player player) {
        PlayerState state = player.getUncommitedState();
        Future<Void> result = Future.future();
        client.preparedQuery(INSERT_PLAYER, Tuple.of(state.playerId, state.screenName), res -> {
            if (res.failed()) {
                result.fail(res.cause());
            } else {
                result.complete();
            }
        });
        return result;
    }

    private Future<Optional<PlayerState>> read(int playerId) {
        Future<PgRowSet> result = Future.future();
        client.preparedQuery(SELECT_PLAYER, Tuple.of(playerId), res -> result.handle(res));

        return result.compose(rows -> {
            PgIterator resultSet = rows.iterator();
            if (!resultSet.hasNext()) {
                return Future.succeededFuture(Optional.empty());
            }
            Tuple row = resultSet.next();
            PlayerState playerState = new PlayerState();
            playerState.playerId = playerId;
            playerState.screenName = row.getString(0);
            return Future.succeededFuture(Optional.of(playerState));
        });
    }
}
