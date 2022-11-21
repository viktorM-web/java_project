CREATE DATABASE payment_system;

CREATE TABLE admin_payment_system
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE user_payment_system
(
    id               SERIAL PRIMARY KEY,
    assistant INT REFERENCES admin_payment_system(id),
    first_name       VARCHAR(128)        NOT NULL,
    last_name        VARCHAR(128)        NOT NULL,
    number_passport  VARCHAR(128) UNIQUE NOT NULL,
    number_telephone VARCHAR(128)

);

CREATE TYPE STATUS AS ENUM ('unblock', 'block');

CREATE TABLE account_payment_system
(
    id      SERIAL PRIMARY KEY,
    user_id INT REFERENCES user_payment_system (id),
    balance NUMERIC NOT NULL,
    status  STATUS
);

CREATE TABLE credit_card
(
    id          SERIAL PRIMARY KEY,
    account_id  INT REFERENCES account_payment_system (id),
    max_payment NUMERIC,
    status      STATUS
);

CREATE TABLE payment
(
    id         SERIAL PRIMARY KEY,
    card_from  INT REFERENCES credit_card (id),
    account_to INT REFERENCES account_payment_system (id),
    sum        NUMERIC   NOT NULL,
    data       TIMESTAMP NOT NULL
);