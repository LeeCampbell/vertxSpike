package com.leecampbell.playerservice;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.cli.CommandLine;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        // Validate args
        CommandLine cmdLine = CommandLineFactory.parseArguments(args);

        // Create config from args
        JsonObject config = VertxConfig.loadConfig(cmdLine.getOptionValue("config"));

        // Print config
        Vertx vertx = Vertx.vertx(new VertxOptions());
        vertx.exceptionHandler(err -> err.printStackTrace());
        VertxConfig.printConfig(vertx, logger);

        // Start server with config
        vertx.deployVerticle(PlayerCommandVerticle.class.getName(),
                new DeploymentOptions().setInstances(VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE).setConfig(config),
                event -> {
                    if (event.succeeded()) {
                        logger.info("Server listening on port " + 8080);
                    } else {
                        logger.error("Unable to start your application", event.cause());
                    }
                });
    }
}