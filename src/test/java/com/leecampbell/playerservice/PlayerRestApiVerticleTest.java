package com.leecampbell.playerservice;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.buffer.impl.BufferFactoryImpl;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@RunWith(VertxUnitRunner.class)
public class PlayerRestApiVerticleTest {

    private Vertx vertx;
    private DatabaseHost databaseHost;

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();

        JsonObject config = new JsonObject()
                //.put("host", "l")
                .put("database", "postgres")
                .put("username", "postgres")
                .put("password", "pwd")
                .put("port", 5432);

        //TODO: Actually hit the DB in the prod code. -LC
        databaseHost = new DatabaseHost(config);
        databaseHost.start();

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(config);

        vertx.deployVerticle(PlayerRestApiVerticle.class.getName(),
                options,
                context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) throws Exception {

        vertx.close(context.asyncAssertSuccess());
        databaseHost.close();
    }

    @Test
    public void givenNoPlayer_whenAddingPlayer_thenCommittedToDB(TestContext context) {
        final Async async = context.async();
        final int playerId = 1;
        final String screenName = "Lee";

        RequestOptions reqOpt = new RequestOptions();
        reqOpt.setHost("localhost");
        reqOpt.setPort(8080);
        reqOpt.setURI("/players/" + playerId);

        context.assertFalse(hasPlayerRow(playerId, screenName), "expected no player row prior to test");

        HttpClient httpClient = vertx.createHttpClient();
        HttpClientRequest request = httpClient.post(reqOpt, httpClientResponse -> {
            context.assertEquals(201, httpClientResponse.statusCode());
            context.assertTrue(hasPlayerRow(playerId, screenName), "player not added to db");
            async.complete();
        });

        Buffer i = new BufferFactoryImpl().buffer("{ \"screenName\" : \"Lee\" }");
        request.putHeader("content-length", "" + i.length());
        request.putHeader("content-type", "application/json");
        request.sendHead();
        request.end("{ \"screenName\" : \"Lee\" }");
    }

    private boolean hasPlayerRow(int playerId, String screenName) {
        DataSource ds = databaseHost.getDataSource();
        try (Connection conn = ds.getConnection()) {
            String sql = "SELECT screen_name from player_service.player where player_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                String actualScreenName = rs.getString(1);
                return Objects.equals(actualScreenName, screenName);
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}