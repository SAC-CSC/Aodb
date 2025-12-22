package com.industrial.AODB.Controller;

import com.industrial.AODB.Entity.Flight;
import com.industrial.AODB.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*") // adjust CORS for your environment
public class FlightController {

    private final FlightService svc;

    public FlightController(FlightService svc) {
        this.svc = svc;
    }

    // GET /api/flights/active
    @GetMapping("/active")
    public ResponseEntity<List<Flight>> getActive() {
        return ResponseEntity.ok(svc.getActiveFlights());
    }

    // POST /api/flights  (insert)
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Flight flight) {
        if (svc.isExistsForInsert(flight)) {
            return ResponseEntity.status(409).body(new ApiResult("error", "Record already exists"));
        }
        Flight saved = svc.insert(flight);
        return ResponseEntity.ok(new ApiResult("success", "Inserted", saved.getRecId()));
    }

    // POST /api/flights/{id}  (update)  or use PUT /api/flights
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Flight flight) {
        flight.setRecId(id);
        if (svc.isExistsForUpdate(flight)) {
            return ResponseEntity.status(409).body(new ApiResult("error", "Only sortPosition can be updated.. record exists"));
        }
        Flight updated = svc.update(flight);
        return ResponseEntity.ok(new ApiResult("success", "Updated", updated.getRecId()));
    }

    // Soft delete: PUT /api/flights/{id}/delete
    @PutMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestBody(required=false) DeleteRequest dr) {
        String operator = (dr != null && dr.getOperatorName() != null) ? dr.getOperatorName() : "unknown";
        svc.softDelete(id, operator);
        return ResponseEntity.ok(new ApiResult("success", "Deleted", id));
    }

    // Simple DTO classes for JSON responses
    static class ApiResult {
        public String status;
        public String message;
        public Object id;
        public ApiResult(String s, String m) { status = s; message = m; }
        public ApiResult(String s, String m, Object id) { status = s; message = m; this.id = id; }
    }

    static class DeleteRequest {
        private String operatorName;
        public String getOperatorName() { return operatorName; }
        public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    }
}


