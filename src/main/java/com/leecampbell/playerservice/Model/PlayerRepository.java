package com.leecampbell.playerservice.Model;

import io.vertx.core.Future;

public interface PlayerRepository {
    Future<Player> get(int id);

    Future save(Player player);
}
