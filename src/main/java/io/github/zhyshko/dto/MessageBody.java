package io.github.zhyshko.dto;

import java.util.List;
import java.util.UUID;

public class MessageBody {

    private UUID chatId;
    private UUID userId;
    private String message;
    private List<Chat> chats;
    private List<String> messages;

    public MessageBody() {

    }

    public MessageBody(UUID chatId, UUID userId, String message, List<Chat> chats, List<String> messages) {
        this.chatId = chatId;
        this.userId = userId;
        this.message = message;
        this.chats = chats;
        this.messages = messages;
    }

    public UUID getChatId() {
        return chatId;
    }

    public void setChatId(UUID chatId) {
        this.chatId = chatId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

}
