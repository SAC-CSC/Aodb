package com.industrial.AODB.Controller;

import com.industrial.AODB.Entity.SortPosition;
import com.industrial.AODB.service.SortPositionService;
import com.industrial.AODB.service.TelegramTriggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sortpositions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SortPositionController {

    @Autowired
    private TelegramTriggerService telegramTriggerService;

    private final SortPositionService svc;

    // ------------------ GET ACTIVE --------------------
    @GetMapping("/active")
    public ResponseEntity<List<SortPosition>> getActive() {
        return ResponseEntity.ok(svc.getActive());
    }

    // ------------------ CREATE -------------------------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody SortPosition s) {

        // Default values
        if (s.getOriginDate() == null) s.setOriginDate(LocalDate.now());
        if (s.getCreated() == null) s.setCreated(LocalDateTime.now());
        if (s.getOperationTime() == null) s.setOperationTime(LocalDateTime.now());
        if (!s.isDeleted()) {
            s.setDeleted(true);
        }

        // Check duplicates
        if (svc.isExistsForInsert(s)) {
            return ResponseEntity.status(409)
                    .body(new ApiResult("error", "Record already exists"));
        }

        SortPosition saved = svc.insert(s);

        return ResponseEntity.ok(new ApiResult("success", "Inserted", saved.getRecId()));
    }

    // ------------------ UPDATE -------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SortPosition s) {
        s.setRecId(id);

        if (svc.isExistsForUpdate(s)) {
            return ResponseEntity.status(409)
                    .body(new ApiResult("error", "Duplicate sortPosition or sortValue exists"));
        }

        s.setLastEditTime(LocalDateTime.now());

        SortPosition updated = svc.update(s);

        return ResponseEntity.ok(new ApiResult("success", "Updated", updated.getRecId()));
    }

    // ------------------ SOFT DELETE --------------------
    @PutMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        svc.softDelete(id);
        return ResponseEntity.ok(new ApiResult("success", "Deleted", id));
    }

    // ------------------ BULK SAVE --------------------
    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestBody List<SortPosition> list) {

        for (SortPosition s : list) {
            // Update record
            if (s.getRecId() != 0) {
                s.setLastEditTime(LocalDateTime.now());
                svc.update(s);
            }
            // Insert new record
            else {
                if (!svc.isExistsForInsert(s)) {
                    if (s.getOriginDate() == null) s.setOriginDate(LocalDate.now());
                    s.setDeleted(false);
                    s.setCreated(LocalDateTime.now());
                    s.setOperationTime(LocalDateTime.now());
                    svc.insert(s);
                }
            }
        }

        telegramTriggerService.sendTriggerToAnt(); // Trigger Ant build
        return ResponseEntity.ok(new ApiResult("success", "All sort positions saved"));
    }

    // ------------------ API RESULT CLASS --------------------
    static class ApiResult {
        public String status;
        public String message;
        public Object id;

        public ApiResult(String s, String m) {
            status = s;
            message = m;
        }

        public ApiResult(String s, String m, Object id) {
            status = s;
            message = m;
            this.id = id;
        }
    }
}
