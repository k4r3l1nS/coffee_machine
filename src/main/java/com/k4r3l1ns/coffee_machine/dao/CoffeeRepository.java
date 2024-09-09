package com.k4r3l1ns.coffee_machine.dao;

import com.k4r3l1ns.coffee_machine.models.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoffeeRepository extends JpaRepository<Coffee, Long> {
    Coffee findByNameIgnoreCase(String name);
    void deleteByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
