package com.industrial.AODB.Controller;

import com.industrial.AODB.service.TcpClientService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

//@RestController
//@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
//@CrossOrigin("*")
public class DashboardRestController12 {

//    @Autowired
    private TcpClientService tcpClientService;

   // @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> data = new HashMap<>();
        data.put("ipAddress", tcpClientService.getIpAddress());
        data.put("port", tcpClientService.getPort());
        data.put("readTimeout", tcpClientService.getReadTimeout());
        data.put("arraySize", tcpClientService.getArraySize());
        data.put("socketExceptionThreshold", tcpClientService.getSocketExceptionThreshold());
        data.put("isOnline", tcpClientService.isConnectionStatus());
        data.put("systemMessage", tcpClientService.isSqlRunningStatus() ? "SQL Server is Running" : "SQL Server is Offline");
        data.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return data;
    }
}
