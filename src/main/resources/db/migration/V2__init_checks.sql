ALTER TABLE coffee_prices
    ADD CONSTRAINT check_price CHECK ( price > 0 );

ALTER TABLE coffee
    ADD CONSTRAINT check_volume CHECK ( volume > 0 );

ALTER TABLE coffee_ingredient
    ADD CONSTRAINT check_price CHECK ( ingredient_value > 0 );

ALTER TABLE ingredient
    ADD CONSTRAINT check_residual_value CHECK ( residual_value >= 0 );

ALTER TABLE "order"
    ADD CONSTRAINT check_portion_coefficient CHECK ( portion_coefficient > 0 );