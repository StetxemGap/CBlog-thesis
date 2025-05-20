package com.korenko.CBlog.DTO;

public class MessageDTO {
    // для получения сообщений с клиента
    private String content;
    private String recipient;

    // для отправки сообщений клиенту
    private String otherUser;

    public MessageDTO(String content, String recipient) {
        this.content = content;
        this.recipient = recipient;
    }

    public String getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(String otherUser) {
        this.otherUser = otherUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
