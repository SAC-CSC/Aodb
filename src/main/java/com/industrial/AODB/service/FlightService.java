// 3. FlightService
package com.industrial.AODB.service;


import com.industrial.AODB.Entity.Flight;
import com.industrial.AODB.Repository.FlightRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository repo;

    public List<Flight> getActiveFlights() {
        return repo.findByDeletedFalseOrderBySortPositionAsc();
    }

    public Optional<Flight> getById(Long id) {
        return repo.findById(id).filter(f -> !f.isDeleted());
    }

    public boolean isExistsForInsert(Flight f) {
        LocalDate od = f.getOriginDate();
        return repo.existsByFlightNumberAndOriginDateAndArrivalAirportAndDeletedFalse(
                f.getFlightNumber(), od, f.getArrivalAirport()
        );
    }


    public boolean isExistsForUpdate(Flight f) {
        LocalDate od = f.getOriginDate();
        Long id = f.getRecId();
        return repo.existsByFlightNumberAndOriginDateAndArrivalAirportAndRecIdNotAndDeletedFalse(
                f.getFlightNumber(), od, f.getArrivalAirport(), id
        );
    }

    @Transactional
    public Flight insert(Flight f) {
        LocalDateTime now = LocalDateTime.now();
        f.setCreated(now);
        f.setOperationTime(now);
        f.setLastEditTime(now);
        f.setDeleted(false);
        return repo.save(f);
    }

    @Transactional
    public Flight update(Flight f) {
        LocalDateTime now = LocalDateTime.now();
        f.setLastEditTime(now);
        return repo.save(f);
    }

    @Transactional
    public void softDelete(Long id, String operator) {
        repo.findById(id).ifPresent(f -> {
            f.setDeleted(true);
            f.setLastEditTime(LocalDateTime.now());
            f.setOperatorName(operator);
            repo.save(f);
        });
    }
}
