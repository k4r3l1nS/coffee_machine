package com.k4r3l1ns.coffee_machine.dao;

import com.k4r3l1ns.coffee_machine.models.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

    @Query(
            """
                SELECT S
                FROM Statistics S
                WHERE S.orderCount > 0
                ORDER BY S.orderCount DESC
                LIMIT :count
            """
    )
    List<Statistics> mostOrderedCoffee(@Param("count") int count);

    @Query(
            """
                SELECT S
                FROM Statistics S
                WHERE S.overallRevenue > 0
                ORDER BY S.overallRevenue DESC
                LIMIT :count
            """
    )
    List<Statistics> mostProfitableCoffee(@Param("count") int count);

    @Query(
            """
                SELECT S
                FROM Statistics S
                WHERE S.portionCount > 0
                ORDER BY S.portionCount DESC
                LIMIT :count
            """
    )
    List<Statistics> mostServedCoffee(@Param("count") int count);
}
