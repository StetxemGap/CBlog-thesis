package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.MessageDTO;
import com.korenko.CBlog.model.MessageEntity;
import com.korenko.CBlog.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/handleMessage")
//    @SendTo("/topic/updates")
    public void handleMessage(@Payload MessageDTO message, Principal principal) {
        String sender = principal.getName();
        String recipientUS = message.getRecipient();
        messageService.saveToDatabase(sender, recipientUS, message.getContent());

        messagingTemplate.convertAndSendToUser(
                sender,
                "/queue/messages",
                message
        );

        messagingTemplate.convertAndSendToUser(
                recipientUS,
                "/queue/messages",
                message
        );
    }

    @MessageMapping("/requestMessages")
    @SendTo("/topic/messages")
    public List<MessageEntity> getMessages(@Payload MessageDTO request, Principal principal) {
        String sender = principal.getName();
        String recipient = request.getOtherUser();
        System.out.println("Recipient name: " + recipient + " sender: " + sender);

        return messageService.getMessagesBetweenUsers(sender, recipient);
    }
}
