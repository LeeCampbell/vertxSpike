FROM postgres:11.1-alpine

COPY ./src/test/resources/db/V1.0__player_service.sql /docker-entrypoint-initdb.d/
