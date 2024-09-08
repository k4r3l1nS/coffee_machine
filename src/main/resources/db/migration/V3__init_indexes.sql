CREATE INDEX idx_order_coffee_id ON "order" (coffee_id);
CREATE INDEX idx_order_created_at ON "order" (created_at);
CREATE INDEX idx_coffee_prices_coffee_id ON coffee_prices (coffee_id);
CREATE INDEX idx_coffee_ingredient_coffee_id ON coffee_ingredient (coffee_id);
CREATE INDEX idx_coffee_ingredient_ingredient_id ON coffee_ingredient (ingredient_id);
CREATE INDEX idx_ingredient_measurement_id ON ingredient (measurement_id);
CREATE INDEX idx_coffee_name ON coffee (name);