package io.github.zhyshko.dto;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.zhyshko.socket.ClientTransiever;

public class Application {

    private static Map<UUID, User> users = new HashMap<>();
    private static Map<UUID, Chat> chats = new HashMap<>();
    private static HashMap<UUID, UUID> userActiveChats = new HashMap<>();

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");

    static {
        Chat broadcast = new Chat();
        broadcast.setName("BROADCAST");
        broadcast.setUuid(UUID.randomUUID());
        broadcast.setBroadcast(true);
        chats.put(broadcast.getUuid(), broadcast);
        addMessageToChat(broadcast.getUuid(), "Current chat: "+broadcast.getName());
    }

    public static synchronized void createChatsCombinationsForNewUser(ClientTransiever newUser) {
        addUserToBroadcast(newUser);

        if (users.size() > 1) {
            for (User user : users.values()) {
                if(user.equals(users.get(newUser.getUserId()))) {
                    continue;
                }
                Chat newChat = new Chat();
                newChat.setName(user.getUsername() + " & " + newUser.getUsername());
                newChat.setUuid(UUID.randomUUID());
                newChat.setUsers(List.of(user.getId(), newUser.getUserId()));
                chats.put(newChat.getUuid(), newChat);
                addMessageToChat(newChat.getUuid(), "Current chat: "+newChat.getName()+" | "+newChat.getUuid());
            }
        }

    }

    private static synchronized void addUserToBroadcast(ClientTransiever newUser) {
        Chat broadcast = chats.values().stream().filter(c -> c.isBroadcast()).findFirst().get();
        broadcast.addUser(newUser.getUserId());
        addMessageToChat(broadcast.getUuid(), "[" + LocalTime.now().format(dtf) + "] " + newUser.getUsername()+" has joined!");
    }

    public static synchronized void newUser(UUID userId, String message) {
        User user = new User(message, userId);
        users.put(userId, user);
    }

    public static synchronized void setUserActiveChat(UUID userId, UUID chatId) {
        userActiveChats.put(userId, chatId);
    }

    public static synchronized long getUserActiveChatHashCode(UUID userId) {
        UUID activeChatId = userActiveChats.get(userId);
        return findChatForUuid(activeChatId).hashCode();
    }

    public static synchronized Chat getActiveChatForUser(UUID userId) {
        return findChatForUuid(userActiveChats.get(userId)).clone();
    }

    public static synchronized void addMessageToChat(UUID chatId, String text) {
        Chat chat = findChatForUuid(chatId);
        chat.addMessage(text);
    }

    public static synchronized List<String> getUserActiveChatMessages(UUID userId) {
        return findChatForUuid(userActiveChats.get(userId)).getMessages();
    }

    private static Chat findChatForUuid(UUID chatId) {
        return chats.get(chatId);

    }

    public static synchronized List<Chat> getChats() {
        return chats.values().stream().collect(Collectors.toList());
    }

    public static synchronized List<Chat> getChatsCopy() {
        return chats.values().stream().map(c -> c.clone()).collect(Collectors.toList());
    }

    public static synchronized List<Chat> getChatsForUser(UUID userId) {
        return chats.values().stream().filter(c -> c.getUsers().contains(userId)).collect(Collectors.toList());
    }

    public static synchronized long getAvailableChatsCount() {
        return chats.keySet().size();
    }

    public static synchronized void logoutUser(UUID userId) {
        userActiveChats.remove(userId);
        Chat broadcast = chats.values().stream().filter(c -> c.isBroadcast()).findFirst().get();
        addMessageToChat(broadcast.getUuid(), "[" + LocalTime.now().format(dtf) + "] " + users.get(userId).getUsername()+" has left!");
        users.remove(userId);
        List<UUID> chatsToRemove = new ArrayList<>();
        for(Chat chat : chats.values()) {
            if(chat.getUsers().contains(userId) && !chat.isBroadcast()) {
                chatsToRemove.add(chat.getUuid());
            }
        }
        chatsToRemove.forEach(chats::remove);
    }

}
