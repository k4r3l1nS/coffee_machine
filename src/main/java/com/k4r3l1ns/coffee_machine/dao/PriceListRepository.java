package com.k4r3l1ns.coffee_machine.dao;

import com.k4r3l1ns.coffee_machine.models.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    PriceList findByCoffeeId(Long id);
}
