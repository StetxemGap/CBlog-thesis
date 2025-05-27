package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.TypingNotification;
import com.korenko.CBlog.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
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

    // принимаем сведения, что клиент где-то на сайте оказался
    @MessageMapping("/register")
    public void registerUser(@Payload String username) {
        onlineUsers.add(username);
        notifyStatusChange(username, true);
    }

    // принимаем сведения, что клиент вышел из аккаунта или закрыл вкладку
    @MessageMapping("/unregister")
    public void unregisterUser(@Payload String username) {
        onlineUsers.remove(username);
        notifyStatusChange(username, false);
    }

    // принимаем запрос от клиента на список пользователей в сети
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

    @MessageMapping("/typing")
    public void handleTyping(@Payload Map<String, Object> payload, Principal principal) {
        boolean typing = Boolean.parseBoolean(payload.get("isTyping").toString());
        String recipient = payload.get("recipient").toString();

        Map<String, Object> response = new HashMap<>();
        response.put("sender", principal.getName());
        response.put("recipient", recipient);
        response.put("isTyping", typing);

        messagingTemplate.convertAndSendToUser(
                recipient,
                "/queue/typing",
                response
        );
    }
}
