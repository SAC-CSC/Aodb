package com.industrial.AODB.Repository;

import com.industrial.AODB.Entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByDeletedFalseOrderBySortPositionAsc();

    boolean existsByFlightNumberAndOriginDateAndArrivalAirportAndDeletedFalse(
            String flightNumber, LocalDate originDate, String arrivalAirport
    );

    boolean existsByFlightNumberAndOriginDateAndArrivalAirportAndRecIdNotAndDeletedFalse(
            String flightNumber, LocalDate originDate, String arrivalAirport, Long recId
    );

}
