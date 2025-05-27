package com.korenko.CBlog.controllers;

import com.korenko.CBlog.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class WebSocketController {

    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepo userRepo;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/register")
    public void registerUser(@Payload String username) {
        System.out.println("Request the user " + username);
        onlineUsers.add(username);
        notifyStatusChange(username, true);
    }

    @MessageMapping("/unregister")
    public void unregisterUser(@Payload String username) {
        onlineUsers.remove(username);
        notifyStatusChange(username, false);
    }

    @MessageMapping("/requestStatuses")
    public void sendStatuses(@Payload String requestingUsername) {
        List<String> allUsers = userRepo.findAllActiveUsernames();

        Map<String, Boolean> userStatuses = new HashMap<>();

        for (String user : allUsers) {
            userStatuses.put(user, onlineUsers.contains(user));
        }
        messagingTemplate.convertAndSendToUser(
                requestingUsername,
                "/queue/statuses",
                userStatuses
        );
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
