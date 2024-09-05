-- Вставка данных в таблицу единиц измерения
INSERT INTO measurement (id, name, context) VALUES
    (1, 'ML', 'Milliliters'),
    (2, 'G', 'Grams');


-- Вставка данных в таблицу кофе
INSERT INTO coffee (id, name, description, volume) VALUES
    (1, 'Cappuccino', 'A coffee-based drink prepared with espresso, hot milk, and steamed milk foam', 180),
    (2, 'Americano', 'A style of coffee prepared by adding hot water to espresso', 250),
    (3, 'Espresso', 'A concentrated form of coffee served in small, strong shots', 30);


-- Вставка данных в таблицу ингредиентов
INSERT INTO ingredient (id, measurement_id, ingredient_name, residual_value) VALUES
    (1, 2, 'Coffee Beans', 18),
    (2, 1, 'Water', 200),
    (3, 1, 'Milk', 150),
    (4, 1, 'Milk Foam', 30);


-- Вставка данных в таблицу, связывающую кофе и ингредиенты
INSERT INTO coffee_ingredient (id, coffee_id, ingredient_id, ingredient_value) VALUES

    -- Ингредиенты для капучино
    (1, 1, 1, 18), -- Кофейные зёрна
    (2, 1, 2, 60), -- Вода
    (3, 1, 3, 120), -- Молоко
    (4, 1, 4, 30), -- Молочная пена

    -- Ингредиенты для американо
    (5, 2, 1, 18), -- Кофейные зёрна
    (6, 2, 2, 230), -- Вода

    -- Ингредиенты для эспрессо
    (7, 3, 1, 18), -- Кофейные зёрна
    (8, 3, 2, 30); -- Вода


-- Вставка данных в таблицу цен на кофе
INSERT INTO coffee_prices (id, coffee_id, price) VALUES
    (1, 1, 200), -- Cappuccino
    (2, 2, 130), -- Americano
    (3, 3, 120); -- Espresso
