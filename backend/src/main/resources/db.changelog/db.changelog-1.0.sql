--liquibase formatted sql


--changeset martin:1
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(40) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    role VARCHAR(32)
);
