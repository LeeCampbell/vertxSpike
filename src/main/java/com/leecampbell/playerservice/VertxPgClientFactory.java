package com.leecampbell.playerservice;

import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgPoolOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class VertxPgClientFactory {

    public static PgClient create(Vertx vertx, String host, String database, String userName, String password, int port) {
        if(vertx==null) throw new IllegalArgumentException("vertx parameter can not be null");
        //if(host==null) throw new IllegalArgumentException("host parameter can not be null");
        if(database==null) throw new IllegalArgumentException("database parameter can not be null");
        if(userName==null) throw new IllegalArgumentException("userName parameter can not be null");
        if(password==null) throw new IllegalArgumentException("password parameter can not be null");

        PgPoolOptions options = new PgPoolOptions();
        options.setDatabase(database);
        //options.setHost(host);
        options.setPort(port);
        options.setUser(userName);
        options.setPassword(password);
        options.setCachePreparedStatements(true);
        return PgClient.pool(vertx, new PgPoolOptions(options).setMaxSize(1));
    }

    public static PgClient create(Vertx vertx, JsonObject config) {
        return create(vertx,
                config.getString("host"),
                config.getString("database"),
                config.getString("username"),
                config.getString("password"),
                config.getInteger("port", 5432));
    }
}
