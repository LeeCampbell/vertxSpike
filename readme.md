# vertx spike

To build (requires Java 8 and Maven installed)

```
mvn clean verify
```

To run
```
mvn clean verify

docker build -f ./docker/postgresdb.dockerfile -t vertxspike-db .
docker run --name vertxSpike-db -d --rm -p 5432:5432 -e POSTGRES_PASSWORD=mysecretpassword vertxspike-db
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



