package com.industrial.AODB.Repository;

import com.industrial.AODB.Entity.SortPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SortPositionRepository extends JpaRepository<SortPosition, Long> {

    List<SortPosition> findByDeletedFalseOrderBySortValueAsc();

    boolean existsBySortPositionAndOriginDateAndDeletedFalse(String sortPosition,
                                                             java.time.LocalDate originDate);

    boolean existsBySortPositionAndOriginDateAndRecIdNotAndDeletedFalse(String sortPosition,
                                                                        java.time.LocalDate originDate,
                                                                        Long recId);
}
