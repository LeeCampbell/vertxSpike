FROM boxfuse/flyway:5.1.4-alpine

COPY ./src/test/resources/db /flyway/sql

ENTRYPOINT [ "sh" ]
#CMD ["-c", "flyway -url=jdbc:postgresql://${DB_SERVER_NAME}/${DB_NAME} -user=${JDBC_USERNAME} -password=${JDBC_PASSWORD} -schemas=wallet_store migrate"]
CMD ["-c", "flyway -url=jdbc:postgresql://localhost:5432/player_service -user=postgres -password=mysecretpassword -schemas=player_service migrate"]
