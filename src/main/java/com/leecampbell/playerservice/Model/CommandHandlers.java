package com.leecampbell.playerservice.Model;

import io.vertx.core.Future;

public class CommandHandlers {

    private final PlayerRepository repository;

    public CommandHandlers(PlayerRepository repository){
        this.repository = repository;
    }

    public Future<Void> handle(RegisterPlayerCommand registerPlayerCommand){
        Future<Player> getPlayer = repository.get(registerPlayerCommand.getPlayerId());
        return getPlayer.compose(player -> {
            player.register(registerPlayerCommand);
            return repository.save(player);
        });
    }

}
