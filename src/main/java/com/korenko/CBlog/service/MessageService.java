package com.korenko.CBlog.service;

import com.korenko.CBlog.model.ChatMessage;
import com.korenko.CBlog.model.MessageEntity;
import com.korenko.CBlog.repo.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    private static final int MAX_MESSAGE_LENGTH = 255;

    // метод сохранения сообщения больше 355 символов
    public List<MessageEntity> saveLongMessageToDatabase(String sender, String recipient, String text) {
        List<String> messagePart = splitMessage(text);
        List<MessageEntity> savedMessage = new ArrayList<>();

        for (String part : messagePart) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setSender(sender);
            messageEntity.setRecipient(recipient);
            messageEntity.setContent(part);
            messageEntity.setTimestamp(LocalDateTime.now());
            messageEntity.setIsPart(true);
            messageEntity.setPartNumber(savedMessage.size() + 1);

            savedMessage.add(messageRepository.save(messageEntity));
        }

        if (!savedMessage.isEmpty()) {
            MessageEntity lastMessage = savedMessage.get(savedMessage.size() - 1);
            lastMessage.setIsLastPart(true);
            messageRepository.save(lastMessage);
        }

        return savedMessage;
    }

    // делим сообщение правильно
    private List<String> splitMessage(String text) {
        List<String> parts = new ArrayList<>();
        if (text.length() <= MAX_MESSAGE_LENGTH) {
            parts.add(text);
            return parts;
        }

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + MAX_MESSAGE_LENGTH, text.length());

            if (end < text.length()) {
                int lastSpace = text.lastIndexOf(' ', end);
                if (lastSpace > start) {
                    end = lastSpace;
                }
            }
            parts.add(text.substring(start, end).trim());
            start = end;
        }
        return parts;
    }

    // метод сохранения сообщения меньше 355 символов
    public MessageEntity saveToDatabase(String sender, String recipient, String text, Boolean isFile) {

        MessageEntity messageEntity = new MessageEntity();

        messageEntity.setSender(sender);
        messageEntity.setRecipient(recipient);
        messageEntity.setContent(text);
        messageEntity.setTimestamp(LocalDateTime.now());
        messageEntity.setIsFile(isFile);

        messageRepository.save(messageEntity);
        return messageEntity;
    }

    public List<MessageEntity> getMessagesBetweenUsers(String user1, String user2) {
        System.out.println("getMessagesBetweenUsers");
        return messageRepository.findMessagesBetweenUsers(user1, user2);
    }
}
