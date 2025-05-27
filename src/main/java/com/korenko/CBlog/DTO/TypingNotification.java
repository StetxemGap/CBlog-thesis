package com.korenko.CBlog.DTO;

public class TypingNotification {
    private String sender;
    private String recipient;
    private boolean isTyping;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        this.isTyping = typing;
    }
}
