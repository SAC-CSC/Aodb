package com.industrial.AODB.service;

import com.industrial.AODB.Entity.SortPosition;
import com.industrial.AODB.Repository.SortPositionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SortPositionService {

    private final SortPositionRepository repo;

    // -------------------- GET ACTIVE --------------------
    public List<SortPosition> getActive() {
        return repo.findByDeletedFalseOrderBySortValueAsc();
    }

    // -------------------- GET BY ID ----------------------
    public Optional<SortPosition> getById(Long id) {
        return repo.findById(id)
                .filter(s -> !s.isDeleted());
    }

    // -------------------- EXISTS FOR INSERT --------------------
    public boolean isExistsForInsert(SortPosition s) {

        // Avoid null date issues
        LocalDate od = s.getOriginDate() != null ? s.getOriginDate() : LocalDate.now();

        return repo.existsBySortPositionAndOriginDateAndDeletedFalse(
                s.getSortPosition(),
                od
        );
    }

    // -------------------- EXISTS FOR UPDATE --------------------
    public boolean isExistsForUpdate(SortPosition s) {

        LocalDate od = s.getOriginDate() != null ? s.getOriginDate() : LocalDate.now();

        return repo.existsBySortPositionAndOriginDateAndRecIdNotAndDeletedFalse(
                s.getSortPosition(),
                od,
                s.getRecId()
        );
    }

    // -------------------- INSERT --------------------
    @Transactional
    public SortPosition insert(SortPosition s) {
        LocalDateTime now = LocalDateTime.now();

        if (s.getOriginDate() == null) {
            s.setOriginDate(LocalDate.now());
        }

        s.setCreated(now);
        s.setOperationTime(now);
        s.setLastEditTime(now);
        s.setDeleted(false);
        return repo.save(s);
    }

    // -------------------- UPDATE --------------------
    @Transactional
    public SortPosition update(SortPosition s) {

        s.setLastEditTime(LocalDateTime.now());
        s.setMessageTimeStamp(LocalDateTime.now());

        return repo.save(s);
    }

    // -------------------- SOFT DELETE --------------------
    @Transactional
    public void softDelete(Long id) {
        repo.findById(id).ifPresent(s -> {

            if (!s.isDeleted()) {  // ✔ Correct boolean accessor
                s.setDeleted(true);
                s.setLastEditTime(LocalDateTime.now());
                repo.save(s);
            }
        });
    }

    // -------------------- SOFT DELETE WITH OPERATOR --------------------
    @Transactional
    public void softDelete(Long id, String operator) {
        repo.findById(id).ifPresent(s -> {

            if (!s.isDeleted()) {
                s.setDeleted(true);
                s.setOperatorName(operator);
                s.setLastEditTime(LocalDateTime.now());
                repo.save(s);
            }
        });
    }
}
