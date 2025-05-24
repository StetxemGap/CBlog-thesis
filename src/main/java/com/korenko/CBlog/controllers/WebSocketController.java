package com.korenko.CBlog.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebSocketController {

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/register")
    public void registerUser(@Payload String username) {
        onlineUsers.add(username);
        notifyStatusChange(username, true);
    }

    @MessageMapping("/unregister")
    public void unregisterUser(@Payload String username) {
        onlineUsers.remove(username);
        notifyStatusChange(username, false);
    }

    private void notifyStatusChange(String username, boolean isOnline) {
        messagingTemplate.convertAndSend(
                "/topic/onlineStatus",
                Map.of("username", username, "isOnline", isOnline)
        );
    }

    public boolean isUserOnline(String username) {
        return onlineUsers.contains(username);
    }
}
