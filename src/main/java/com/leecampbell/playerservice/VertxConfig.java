package com.leecampbell.playerservice;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import io.vertx.core.logging.Logger;

public class VertxConfig {

    public static JsonObject loadConfig(String configPath) throws IOException {
        File configFile = new File(configPath);
        return new JsonObject(new String(Files.readAllBytes(configFile.toPath())));
    }

    public static void printConfig(Vertx vertx, Logger logger) {
        boolean nativeTransport = vertx.isNativeTransportEnabled();
        String version = "unknown";
        try {
            InputStream in = Vertx.class.getClassLoader().getResourceAsStream("META-INF/vertx/vertx-version.txt");
            if (in == null) {
                in = Vertx.class.getClassLoader().getResourceAsStream("vertx-version.txt");
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[256];
            while (true) {
                int amount = in.read(buffer);
                if (amount == -1) {
                    break;
                }
                out.write(buffer, 0, amount);
            }
            version = out.toString();
        } catch (IOException e) {
            logger.error("Could not read Vertx version", e);
        }
        logger.info("Vertx: " + version);
        logger.info("Event Loop Size: " + VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE);
        logger.info("Native transport : " + nativeTransport);
    }
}
