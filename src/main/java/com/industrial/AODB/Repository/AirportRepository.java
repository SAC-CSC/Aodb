package com.industrial.AODB.Repository;



import com.industrial.AODB.Entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
    boolean existsByAirlineAndFlightNumberAndDestination(String airline, String flightNumber, String destination);
}


