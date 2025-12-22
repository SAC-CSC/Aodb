package com.industrial.AODB.Repository;

import com.industrial.AODB.Entity.AirlineAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AirlineAllocationRepository extends JpaRepository<AirlineAllocation, Long> {

    List<AirlineAllocation> findByDeletedFalseOrderBySortPositionAsc();

    boolean existsByAirlineCodeAndOriginDateAndOperationAndDeletedFalse(
            String airlineCode, LocalDate originDate, String operation
    );

    boolean existsByAirlineCodeAndOriginDateAndOperationAndRecIdNotAndDeletedFalse(
            String airlineCode, LocalDate originDate, String operation, Long recId
    );
}
