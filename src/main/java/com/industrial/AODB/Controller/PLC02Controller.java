package com.industrial.AODB.Controller;


import com.industrial.AODB.service.Plc02Service;
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
@RequestMapping("/api/status/plc02") // Match Angular API_URL
@RequiredArgsConstructor
@CrossOrigin("*")
public class PLC02Controller {

    @Autowired
    private Plc02Service plc02Service;

    @GetMapping
    public Map<String, Object> getStatus() {
        Map<String, Object> data = new HashMap<>();

        // PLC Info
        data.put("ipAddress", plc02Service.getIpAddress());
        data.put("port", plc02Service.getPort());
        data.put("readTimeout", plc02Service.getReadTimeout());

        // System Info
        data.put("systemMessage", plc02Service.isSqlRunningStatus() ? "SQL Server is Running" : "SQL Server is Offline");
        data.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Statuses
        data.put("plcReachable", plc02Service.isConnectionStatus()); // Angular $scope.isOnline
        data.put("serviceRunning", plc02Service.isServiceRunning()); // Angular $scope.serviceRunning
        data.put("sqlHealthy", plc02Service.isSqlRunningStatus()); // Angular $scope.sqlHealthy

        return data;
    }
}
