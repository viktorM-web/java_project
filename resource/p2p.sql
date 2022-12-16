CREATE DATABASE p2p_system;

CREATE TABLE user_p2p
(
    id               SERIAL PRIMARY KEY,
    first_name       VARCHAR(128)        NOT NULL,
    last_name        VARCHAR(128)        NOT NULL,
    number_passport  VARCHAR(128) UNIQUE NOT NULL,
    email            VARCHAR(128),
    number_telephone VARCHAR(128),
    admin            BOOLEAN
);

-- CREATE TYPE CURRENCY AS ENUM ('RUB', 'USD', 'EUR', 'BTC', 'ETH', 'USDT');

CREATE TABLE card
(
    id_number BIGINT PRIMARY KEY,
    owner     INT REFERENCES user_p2p (id),
    validity  DATE NOT NULL,
    balance   NUMERIC  NOT NULL,
    currency  VARCHAR(5)
);

CREATE TABLE crypto_wallet
(
    id_number VARCHAR PRIMARY KEY,
    owner     INT REFERENCES user_p2p (id)
);

CREATE TABLE wallet_content
(
    id             SERIAL PRIMARY KEY,
    id_number      VARCHAR REFERENCES crypto_wallet (id_number),
    cryptocurrency VARCHAR(5),
    amount         INT

);
CREATE TABLE address_wallet
(
    id      INT REFERENCES wallet_content (id),
    net     VARCHAR NOT NULL,
    address VARCHAR NOT NULL
);

-- CREATE TYPE OPERATION AS ENUM ('SELL', 'BUY');

CREATE TABLE offer
(
    id                SERIAL PRIMARY KEY,
    supplier          INT REFERENCES user_p2p (id),
    sum               INT      NOT NULL,
    currency          VARCHAR(5) NOT NULL,
    price             INT      NOT NULL,
    expected_currency VARCHAR(5) NOT NULL,
    publication       DATE     NOT NULL,
    operation         VARCHAR(5)
);

-- CREATE TYPE STATUS AS ENUM ('PROCESSED', 'DISMISSED', 'SUCCESS');

CREATE TABLE transaction
(
    id        SERIAL PRIMARY KEY,
    offer     INT REFERENCES offer (id),
    consumer  INT REFERENCES user_p2p (id),
    condition VARCHAR(5) NOT NULL
);