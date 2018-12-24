package com.leecampbell.playerservice;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import io.vertx.core.json.JsonObject;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.IOException;

final class DatabaseHost implements AutoCloseable {

    private final String userName;
    private final String databaseName;
    private final int port;
    private EmbeddedPostgres db;

    DatabaseHost(JsonObject config) {
        userName = config.getString("username");
        databaseName = config.getString("database");
        port = config.getInteger("port");
    }

    void start() throws IOException {
        if (db != null) throw new IllegalStateException("Already started.");
        db = EmbeddedPostgres.builder()
                .setPort(port)
                .start();

        DataSource dataSource = getDataSource();

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.setLocations("/db");
        flyway.setSchemas("player_service");
        flyway.clean();
        flyway.migrate();
    }

    DataSource getDataSource(){
        return db.getDatabase(
                userName,
                databaseName
        );
    }

    @Override
    public void close() throws Exception {
        if(db!=null) {
            db.close();
        }
    }
}
