package com.k4r3l1ns.coffee_machine.mappers;

import com.k4r3l1ns.coffee_machine.dto.StatisticsDto;
import com.k4r3l1ns.coffee_machine.models.Statistics;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface StatisticsMapper {
    StatisticsDto toDto(Statistics statistics);
}
