# vertx spike

To build (requires Java 8 and Maven installed)

```
mvn clean verify
```

To run
```
mvn clean verify
java -jar target/player-service-1.0-SNAPSHOT-fat.jar -c ./src/main/conf/config.json
```
Then the web server will be listening on `http://localhost:8080`.

