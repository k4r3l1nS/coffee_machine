CREATE TABLE coffee (
    id BIGINT PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(256),
    volume DOUBLE PRECISION NOT NULL
);


CREATE TABLE coffee_prices (
    id BIGINT PRIMARY KEY,
    coffee_id BIGINT NOT NULL UNIQUE,
    price DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (coffee_id) REFERENCES coffee(id)
);

CREATE TABLE measurement (
    id BIGINT PRIMARY KEY,
    name VARCHAR(5) NOT NULL,
    context VARCHAR(64)
);

CREATE TABLE ingredient (
    id BIGINT PRIMARY KEY,
    measurement_id BIGINT NOT NULL,
    ingredient_name VARCHAR(32) NOT NULL UNIQUE,
    residual_value DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (measurement_id) REFERENCES measurement(id)
);

CREATE TABLE coffee_ingredient (
    id BIGINT PRIMARY KEY,
    coffee_id BIGINT NOT NULL,
    ingredient_id BIGINT NOT NULL,
    ingredient_value DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (coffee_id) REFERENCES coffee(id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
);

CREATE TABLE "order" (
    id BIGINT PRIMARY KEY,
    coffee_id BIGINT NOT NULL,
    portion_coefficient DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (coffee_id) REFERENCES coffee(id)
);

CREATE SEQUENCE coffee_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE coffee_prices_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE measurement_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE ingredient_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE coffee_ingredient_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE order_seq
    START WITH 1
    INCREMENT BY 50;
