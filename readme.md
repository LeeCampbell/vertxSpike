# vertx spike

To build (requires Java 8 and Maven installed)

```
mvn clean verify
```

To run
```
mvn clean verify

docker run --name vertxSpike-db -d --rm -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword postgres:11.1-alpine

# dont mount the vol, requires abs path. -LC
# copy the file there instead.
docker run --rm -v c:/users/leery/source/repos/vertxSpike/src/test/resources/db:/flyway/sql boxfuse/flyway:5.1.4-alpine -url=jdbc:postgresql://localhost:5432/player_service -user=postgres -password=mysecretpassword migrate

java -jar target/player-service-1.0-SNAPSHOT-fat.jar -c ./src/main/conf/config.json

```
Then the web server will be listening on `http://localhost:8080`.

To create a player issue a post 

PowerShell:
```
Invoke-WebRequest -ContentType "application/json" -Headers @{"accept"="application/json"} -Method Post -InFile "player-lee.json" -UseBasicParsing -Uri "http://localhost:8080/players/1"
```

bash:
```
curl -i -H "Content-Type: application/json" -X POST -d @player-lee.json http://localhost:8080/players/1
```



