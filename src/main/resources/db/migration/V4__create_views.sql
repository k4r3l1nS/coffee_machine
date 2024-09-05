CREATE VIEW v_coffee_stats AS (
    WITH res AS (
        SELECT
            C.id AS coffee_id,
            C.name AS coffee_name,
            sum(CP.price * O.portion_coefficient) AS overall_revenue,
            count(*) AS order_count,
            sum(O.portion_coefficient) AS portion_count
        FROM coffee C
        JOIN "order" O ON C.id = O.coffee_id
        JOIN coffee_prices CP ON C.id = CP.coffee_id
        WHERE created_at > CURRENT_DATE - INTERVAL '5 years'
        GROUP BY C.id
    )

    SELECT
        C.id,
        C.name,
        COALESCE(R.overall_revenue, 0) AS overall_revenue,
        COALESCE(R.order_count, 0) AS order_count,
        COALESCE(R.portion_count, 0) AS portion_count
    FROM coffee C
    LEFT JOIN res R ON C.id = R.coffee_id
);
