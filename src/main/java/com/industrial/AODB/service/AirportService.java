package com.industrial.AODB.service;


import com.industrial.AODB.Entity.Airport;
import com.industrial.AODB.Repository.AirportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AirportService {

    private final AirportRepository repo;

    public AirportService(AirportRepository repo) {
        this.repo = repo;
    }

    public List<Airport> getAll() {
        return repo.findAll();
    }

    public Airport add(Airport airport, String operator) {
        if (repo.existsByAirlineAndFlightNumberAndDestination(
                airport.getAirline(), airport.getFlightNumber(), airport.getDestination())) {
            throw new RuntimeException("Record already exists!");
        }

        String now = LocalDateTime.now().toString();
        airport.setCreated(now);
        airport.setOperationTime(now);
        airport.setOperatorName(operator);
        return repo.save(airport);
    }

    public Airport update(Long recId, Airport updated, String operator) {
        return repo.findById(recId).map(existing -> {
            // ✅ Only allow sortPosition updates, mimic C# logic
            if (!existing.getAirline().equals(updated.getAirline()) ||
                    !existing.getFlightNumber().equals(updated.getFlightNumber()) ||
                    !existing.getDestination().equals(updated.getDestination()) ||
                    !existing.getOriginDate().equals(updated.getOriginDate())) {
                throw new RuntimeException("Only Sort Position can be updated. Record already exists!");
            }
            existing.setSortPosition(updated.getSortPosition());
            existing.setLastEditTime(LocalDateTime.now().toString());
            existing.setOperatorName(operator);
            return repo.save(existing);
        }).orElseThrow(() -> new RuntimeException("Record not found!"));
    }

    public void delete(Long recId) {
        if (!repo.existsById(recId)) {
            throw new RuntimeException("Record not found!");
        }
        repo.deleteById(recId);
    }
}
