package com.industrial.AODB.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
@Getter
@Setter
@Service
public class Plc01Service {

    @Value("${plc01.ipAddress}")
    private String ipAddress;

    @Value("${plc01.port}")
    private int port;

    @Value("${plc01.telegram-read-time-out}")
    private int readTimeout;

    @Value("${plc01.ping-interval:10000}")
    private int pingInterval;

    @Value("${plc01.sql-status-check-interval:10000}")
    private int sqlStatusCheckInterval;

    @Value("${plc01.service-name:CSC_SAC_Service}")  // Add your service name here
    private String serviceName;

    private boolean connectionStatus = false;   // PLC connection
    private boolean sqlRunningStatus = false;   // SQL Server status
    private boolean serviceRunningStatus = false; // Windows service status
    private int failureCount = 0;

    /**
     * Periodically ping the PLC to check connectivity
     */
    @Scheduled(fixedRateString = "${plc01.ping-interval:10000}") // Default every 10s
    public void pingPlc() {
        log.info("Pinging PLC {}:{} with timeout {}", ipAddress, port, readTimeout);
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ipAddress, port), readTimeout);
            connectionStatus = true;
            failureCount = 0;
            log.info("✅ PLC connection successful: {}:{}", ipAddress, port);
        } catch (IOException e) {
            failureCount++;
            connectionStatus = false;
            log.warn("❌ PLC connection failed (attempt {}): {}:{} - {}", failureCount, ipAddress, port, e.getMessage());
        }
    }

    /**
     * Periodically check SQL Server running status
     */
    @Scheduled(fixedRateString = "${plc01.sql-status-check-interval:10000}") // Default every 10s
    public void checkSqlStatus() {
        try {
            boolean sqlRunning = true; // Replace with actual SQL status check
            sqlRunningStatus = sqlRunning;
            log.info(sqlRunning ? "✅ SQL Server is running (PLC01)" : "⚠️ SQL Server is not running (PLC01)");
        } catch (Exception e) {
            sqlRunningStatus = false;
            log.error("❌ Error checking SQL Server status (PLC01): {}", e.getMessage());
        }
    }

    /**
     * Periodically check if the specified Windows Service is running
     */
    @Scheduled(fixedRateString = "${sac.service-status-check-interval:10000}") // Default every 10s
    public void checkWindowsServiceStatus() {
        try {
            Process process = Runtime.getRuntime().exec("sc query " + serviceName);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                boolean running = false;

                while ((line = reader.readLine()) != null) {
                    if (line.contains("RUNNING")) {
                        running = true;
                        break;
                    }
                }

                serviceRunningStatus = running;
                if (running) {
                    log.info("✅ Windows Service '{}' is RUNNING", serviceName);
                } else {
                    log.warn("⚠️ Windows Service '{}' is UNHEALTHY or STOPPED", serviceName);
                }
            }
        } catch (IOException e) {
            serviceRunningStatus = false;
            log.error("❌ Error checking Windows service '{}': {}", serviceName, e.getMessage());
        }
    }

    /**
     * Returns true if PLC is currently reachable
     */
    public boolean isPlcReachable() {
        return connectionStatus;
    }

    /**
     * Returns true if the service is running
     */
    public boolean isServiceRunning() {
        return serviceRunningStatus;
    }
}
