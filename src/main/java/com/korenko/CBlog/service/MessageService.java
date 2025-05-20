package com.korenko.CBlog.service;

import com.korenko.CBlog.model.ChatMessage;
import com.korenko.CBlog.model.MessageEntity;
import com.korenko.CBlog.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void saveToDatabase(String sender, String recipient, String text) {

        MessageEntity messageEntity = new MessageEntity();

        messageEntity.setSender(sender);
        messageEntity.setRecipient(recipient);
        messageEntity.setContent(text);
        messageEntity.setTimestamp(LocalDateTime.now());

        messageRepository.save(messageEntity);
    }

    public List<MessageEntity> getMessagesBetweenUsers(String user1, String user2) {
        System.out.println("getMessagesBetweenUsers");
        return messageRepository.findMessagesBetweenUsers(user1, user2);
    }
}
