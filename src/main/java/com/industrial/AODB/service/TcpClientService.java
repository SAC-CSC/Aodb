package com.industrial.AODB.service;

import com.industrial.AODB.util.XmlHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

@Slf4j
@Service
public class TcpClientService {


    @Value("${aodb.ipaddress}")
    private String ipAddress ;

    @Value("${aodb.port}")
    private int  port ;

    @Value("${aodb.telegram-read-timeout}")
    private int  readTimeout  ;

    @Value("${aodb.array-size}")
    private int  arraySize ;


    @Value("${aodb.socket-exception-thresold:3}")
    private int  socketExceptionThreshold ;


    private boolean connectionStatus = false ;
    private boolean dataOnOffStatus = false ;
    private boolean sqlRunningStatus = false ;

    private int socketExceptionCounter = 0 ;

    public String getIpAddress() { return ipAddress; }
    public int getPort() { return port; }
    public int getReadTimeout() { return readTimeout; }
    public int getArraySize() { return arraySize; }
    public int getSocketExceptionThreshold() { return socketExceptionThreshold; }
    public boolean isConnectionStatus() { return connectionStatus; }
    public boolean isSqlRunningStatus() { return sqlRunningStatus; }

    @PostConstruct
    public void start(){
        new Thread(this:: connectAndListen).start() ;
    }

    ;
    @Autowired
    private XmlHandler xmlHandler;

    private void connectAndListen(){
        while(!connectionStatus){
            try(Socket socket = new Socket()) {
                log.info("Attempting connection to {} : {}", ipAddress, port);
                socket.connect(new InetSocketAddress(ipAddress, port), 5000);
                socket.setSoTimeout(readTimeout);
                connectionStatus = true;
                socketExceptionCounter = 0;
                showConnectedStatus();

                InputStream inputStream = socket.getInputStream();
                byte[] buffer = new byte[arraySize];
                while (connectionStatus && socketExceptionCounter <= socketExceptionThreshold) {
                    try {
                        if (dataOnOffStatus) {
                            int readBytes = inputStream.read(buffer);
                            if (readBytes > 0) {
                                byte[] received = Arrays.copyOf(buffer, readBytes);
                                String message = new String(received).trim();
                                log.info("Received: {}", message);
                                // process telegram

                                 xmlHandler.handleIncomingTelegram(message) ;
                            }
                        }
                    } catch (Exception e) {
                        socketExceptionCounter++;
                        log.error("Exception in read loop: {}", e.getMessage());
                        if (socketExceptionCounter >= socketExceptionThreshold) {
                            showDisconnectedStatus();
                            connectionStatus = false;
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error("Failed to connect or communicate: {}", e.getMessage());
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ignored) {}
            }
        }
    }
    private void showConnectedStatus(){
        log.info("connected to {}:{}" , ipAddress,port) ;
    }

    private void showDisconnectedStatus(){
        log.warn("Disconnected from {}:{}" , ipAddress,port) ;
    }

    @Scheduled(fixedRateString = "${aodb.sql-status-check-interval:10000}")
    public void checkSqlStatus() {
        try {
            // This is platform-specific. A real implementation would use JNA or call `sc query` on Windows
            boolean sqlRunning = true; // Placeholder for actual SQL service status check

            if (sqlRunning) {
                sqlRunningStatus = true;
                dataOnOffStatus = true;
                log.info("SQL Server is running");
            } else {
                sqlRunningStatus = false;
                dataOnOffStatus = false;
                log.warn("SQL Server is not running");
            }
        } catch (Exception e) {
            sqlRunningStatus = false;
            dataOnOffStatus = false;
            log.error("Error checking SQL Server status: {}", e.getMessage());
        }
    }
}
