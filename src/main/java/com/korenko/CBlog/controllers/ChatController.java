package com.korenko.CBlog.controllers;

import com.korenko.CBlog.DTO.MessageDTO;
import com.korenko.CBlog.model.MessageEntity;
import com.korenko.CBlog.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/handleMessage")
    public void handleMessage(@Payload MessageDTO message, Principal principal) {
        String sender = principal.getName();
        String recipientUS = message.getRecipient();

        if (message.getContent().length() > 255) {
            List<MessageEntity> messageParts = messageService.saveLongMessageToDatabase(
                    sender,
                    recipientUS,
                    message.getContent()
            );

            for (MessageEntity part : messageParts) {
                messagingTemplate.convertAndSendToUser(
                        recipientUS,
                        "/queue/newMessages",
                        part
                );
                messagingTemplate.convertAndSendToUser(
                        sender,
                        "/queue/newMessages",
                        part
                );
            }
        } else {

            MessageEntity savedMessage = messageService.saveToDatabase(
                    sender,
                    recipientUS,
                    message.getContent(),
                    false
            );
            messagingTemplate.convertAndSendToUser(
                    sender,
                    "/queue/newMessages",
                    savedMessage
            );

            messagingTemplate.convertAndSendToUser(
                    recipientUS,
                    "/queue/newMessages",
                    savedMessage
            );
        }
    }

    @MessageMapping("/requestMessages")
    public void getMessages(@Payload MessageDTO request, Principal principal) {
        String sender = principal.getName();
        String recipient = request.getOtherUser();

        messagingTemplate.convertAndSendToUser(
                sender,
                "/queue/messages",
                messageService.getMessagesBetweenUsers(sender, recipient)
        );
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file,
                                              @RequestParam("recipient") String recipient,
                                              Principal principal) {
        try {
            String sender = principal.getName();
            String uploadDir = "uploads/";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Создаем сообщение о файле
            String fileLink = "/download/" + fileName;
            String messageContent = "Файл: <a href=\"" + fileLink + "\" download>" + file.getOriginalFilename() + "</a>";

            // Сохраняем в базу данных
            MessageEntity savedMessage = messageService.saveToDatabase(
                    sender,
                    recipient,
                    messageContent,
                    true
            );

            // Отправляем получателю
            messagingTemplate.convertAndSendToUser(
                    recipient,
                    "/queue/newMessages",
                    savedMessage
            );

            // Отправляем отправителю
            messagingTemplate.convertAndSendToUser(
                    sender,
                    "/queue/newMessages",
                    savedMessage
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "filePath", fileLink,
                    "message", savedMessage
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false));
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/" + filename);
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @MessageMapping("/operation")
    public void deleteMessage(@Payload Integer id) {
        MessageEntity message = messageService.getMessageById(id);
        String sender = message.getSender();
        String recipient = message.getRecipient();

        messageService.deleteMessage(id);

        messagingTemplate.convertAndSendToUser(
                sender,
                "/queue/messages",
                messageService.getMessagesBetweenUsers(sender, recipient)
        );

        messagingTemplate.convertAndSendToUser(
                recipient,
                "/queue/messages",
                messageService.getMessagesBetweenUsers(sender, recipient)
        );
    }
}
