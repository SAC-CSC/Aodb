package com.industrial.AODB.service;

import com.industrial.AODB.Entity.AirlineAllocation;
import com.industrial.AODB.Repository.AirlineAllocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AirlineAllocationService {

    private final AirlineAllocationRepository repository;

    public List<AirlineAllocation> getActiveAirlines() {
        return repository.findByDeletedFalseOrderBySortPositionAsc();
    }

    public AirlineAllocation insert(AirlineAllocation a) {
        if (a.getRecId() != null) a.setRecId(null);
        a.setCreated(LocalDateTime.now());
        a.setLastEditTime(LocalDateTime.now());
        a.setDeleted(false);
        return repository.save(a);
    }

    public AirlineAllocation update(AirlineAllocation a) {
        a.setLastEditTime(LocalDateTime.now());
        return repository.save(a);
    }

    public boolean isExistsForInsert(AirlineAllocation a) {
        return repository.existsByAirlineCodeAndOriginDateAndOperationAndDeletedFalse(
                a.getAirlineCode(), a.getOriginDate(), a.getOperation()
        );
    }

    public boolean isExistsForUpdate(AirlineAllocation a) {
        return repository.existsByAirlineCodeAndOriginDateAndOperationAndRecIdNotAndDeletedFalse(
                a.getAirlineCode(), a.getOriginDate(), a.getOperation(), a.getRecId()
        );
    }

    public void softDelete(Long id) {
        repository.findById(id).ifPresent(a -> {
            a.setDeleted(true);
            a.setLastEditTime(LocalDateTime.now());
            repository.save(a);
        });
    }

    public Optional<AirlineAllocation> getById(Long id) {
        return repository.findById(id);
    }
}
