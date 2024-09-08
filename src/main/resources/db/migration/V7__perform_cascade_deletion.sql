ALTER TABLE coffee_prices
    DROP CONSTRAINT coffee_prices_coffee_id_fkey;

ALTER TABLE coffee_prices
    ADD CONSTRAINT coffee_prices_coffee_id_fkey FOREIGN KEY (coffee_id)
        REFERENCES coffee(id)
        ON DELETE CASCADE;


ALTER TABLE coffee_ingredient
    DROP CONSTRAINT coffee_ingredient_coffee_id_fkey;

ALTER TABLE coffee_ingredient
    ADD CONSTRAINT coffee_ingredient_coffee_id_fkey FOREIGN KEY (coffee_id)
        REFERENCES coffee(id)
        ON DELETE CASCADE;


ALTER TABLE "order"
    DROP CONSTRAINT order_coffee_id_fkey;

ALTER TABLE "order"
    ADD CONSTRAINT order_coffee_id_fkey FOREIGN KEY (coffee_id)
        REFERENCES coffee(id)
        ON DELETE CASCADE;