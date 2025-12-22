package com.industrial.AODB.Controller;

import com.industrial.AODB.Entity.AirlineAllocation;
import com.industrial.AODB.service.AirlineAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.industrial.AODB.service.TelegramTriggerService;
@RestController
@RequestMapping("/api/airlines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AirlineAllocationController {

    @Autowired
    private TelegramTriggerService telegramTriggerService;

    private final AirlineAllocationService svc;

    // ------------------ GET ACTIVE --------------------
    @GetMapping("/active")
    public ResponseEntity<List<AirlineAllocation>> getActive() {
        return ResponseEntity.ok(svc.getActiveAirlines());
    }

    // ------------------ CREATE -------------------------
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AirlineAllocation a) {

        // 🔹 Set defaults for required fields
        if (a.getOriginDate() == null) a.setOriginDate(LocalDate.now());
        if (a.getOperation() == null || a.getOperation().isEmpty()) a.setOperation("DEFAULT");
        if (a.getDeleted() == null) a.setDeleted(false);
        if (a.getEnableStatus() == null) a.setEnableStatus(true); // enable by default
        if (a.getCreated() == null) a.setCreated(LocalDateTime.now());

        // Check if record exists
        if (svc.isExistsForInsert(a)) {
            return ResponseEntity.status(409)
                    .body(new ApiResult("error", "Record already exists"));
        }

        AirlineAllocation saved = svc.insert(a);

        return ResponseEntity.ok(new ApiResult("success", "Inserted", saved.getRecId()));
    }



    // ------------------ UPDATE -------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AirlineAllocation a) {
        a.setRecId(id);

        // Only allow update if sortPosition is changing
        if (svc.isExistsForUpdate(a)) {
            return ResponseEntity.status(409)
                    .body(new ApiResult("error", "Only sortPosition can be updated. Record exists"));
        }

        AirlineAllocation updated = svc.update(a);
        return ResponseEntity.ok(new ApiResult("success", "Updated", updated.getRecId()));
    }

    // ------------------ SOFT DELETE --------------------
    @PutMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        svc.softDelete(id);
        return ResponseEntity.ok(new ApiResult("success", "Deleted", id));
    }

    // ------------------ BULK SAVE / UPDATE --------------------
    @PostMapping("/saveAll")
    public ResponseEntity<?> saveAll(@RequestBody List<AirlineAllocation> airlines) {
        for (AirlineAllocation a : airlines) {
            // If recId exists → UPDATE
            if (a.getRecId() != null) {
                svc.update(a);
            }
            // If new → INSERT with defaults
            else {
                if (!svc.isExistsForInsert(a)) {
                    if (a.getOriginDate() == null) a.setOriginDate(LocalDate.now());
                    if (a.getOperation() == null || a.getOperation().isEmpty()) a.setOperation("DEFAULT");
                    a.setDeleted(false);

                    svc.insert(a);
                }
            }
        }
        telegramTriggerService.sendTriggerToAnt(); // 🔥 TRIGGER APACHE ANT
        return ResponseEntity.ok(new ApiResult("success", "All airlines saved"));
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
