package com.industrial.AODB.Controller;

import com.industrial.AODB.service.Plc01Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/status/plc01") // Match Angular API_URL
@RequiredArgsConstructor
@CrossOrigin("*")
public class PLCController {

    @Autowired
    private Plc01Service plc01Service;

    @GetMapping
    public Map<String, Object> getStatus() {
        Map<String, Object> data = new HashMap<>();

        // PLC Info
        data.put("ipAddress", plc01Service.getIpAddress());
        data.put("port", plc01Service.getPort());
        data.put("readTimeout", plc01Service.getReadTimeout());

        // System Info
        data.put("systemMessage", plc01Service.isSqlRunningStatus() ? "SQL Server is Running" : "SQL Server is Offline");
        data.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Statuses
        data.put("plcReachable", plc01Service.isConnectionStatus()); // Angular $scope.isOnline
        data.put("serviceRunning", plc01Service.isServiceRunning()); // Angular $scope.serviceRunning
        data.put("sqlHealthy", plc01Service.isSqlRunningStatus()); // Angular $scope.sqlHealthy

        return data;
    }
}
