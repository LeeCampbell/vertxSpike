CREATE SCHEMA IF NOT EXISTS player_service;
CREATE TABLE player_service.player (
    player_id integer PRIMARY KEY,
    screen_name text NOT NULL
);