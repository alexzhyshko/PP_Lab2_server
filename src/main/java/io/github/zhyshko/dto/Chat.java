package io.github.zhyshko.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Chat{

    private List<String> messages = new ArrayList<>();
    private UUID uuid;
    private String name;
    private List<UUID> users = new ArrayList<>();
    private boolean broadcast;

    public Chat() {
    }

    public synchronized List<String> getMessages() {
        return messages;
    }

    public synchronized void setMessages(List<String> messages) {
        List<String> oldValue = new ArrayList<>();
        oldValue.addAll(this.messages);
        this.messages.clear();
        this.messages.addAll(messages);
    }

    public synchronized void addMessage(String message) {
        List<String> oldValue = new ArrayList<>();
        oldValue.addAll(this.messages);
        this.messages.add(message);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public List<UUID> getUsers() {
        return users;
    }

    public void setUsers(List<UUID> users) {
        this.users = users;
    }



    @Override
    public int hashCode() {
        return Objects.hash(broadcast, messages, name, uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Chat other = (Chat) obj;
        return broadcast == other.broadcast && Objects.equals(messages, other.messages)
                && Objects.equals(name, other.name) && Objects.equals(uuid, other.uuid);
    }

    @Override
    protected Chat clone() {
        Chat chat = new Chat();
        chat.setUuid(this.uuid);
        chat.setBroadcast(this.broadcast);
        chat.setName(this.name);
        List<String> messagesCloned = new ArrayList<>();
        messagesCloned.addAll(this.messages);
        chat.setMessages(messagesCloned);
        List<UUID> usersCloned = new ArrayList<>();
        usersCloned.addAll(this.users);
        chat.setUsers(usersCloned);
        return chat;
    }

    public void addUser(UUID userId) {
        this.users.add(userId);
    }

}
