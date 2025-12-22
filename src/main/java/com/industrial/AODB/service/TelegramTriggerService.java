package com.industrial.AODB.service;

import java.io.OutputStream;
import java.net.Socket;

import org.springframework.stereotype.Service;

@Service   // <- This makes Spring manage it
public class TelegramTriggerService {

    public void sendTriggerToAnt() {
        try (Socket socket = new Socket("127.0.0.1", 6000)) {
            OutputStream os = socket.getOutputStream();
            os.write("START_SENDING".getBytes());
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
