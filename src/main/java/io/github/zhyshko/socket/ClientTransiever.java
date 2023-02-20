package io.github.zhyshko.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import io.github.zhyshko.dto.Application;
import io.github.zhyshko.dto.Chat;
import io.github.zhyshko.dto.Message;
import io.github.zhyshko.dto.MessageBody;
import io.github.zhyshko.dto.MessageType;

public class ClientTransiever implements Runnable {

    private final Socket socket;
    private UUID userId;
    private String username;

    private DataInputStream in;
    private DataOutputStream out;

    private List<Message> outcomingMessage = new ArrayList<>();
    private Message incomingMessage;

    private long lastActiveChatHashCode = 0;
    private long lastAvailableChatsCount = Application.getAvailableChatsCount();

    private Gson gson = new Gson();

    private boolean loggedOut = false;

    public ClientTransiever(Socket socket) {
        this.socket = socket;
        this.userId = UUID.randomUUID();
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        while (!loggedOut) {
            checkIfActiveChatIsUpdated();

            checkIfAvailableChatsCountUpdated();

            try {
                while (!outcomingMessage.isEmpty()) {
                    Message message = outcomingMessage.get(0);
                    System.out.println((Thread.currentThread().getId()) + " | " + gson.toJson(message));
                    out.writeUTF(gson.toJson(message));
                    out.flush();
                    outcomingMessage.remove(message);
                }
                String incomingText = in.readUTF();
                if (incomingText != null) {
                    System.out.println((Thread.currentThread().getId()) + " | " + incomingText);
                    incomingMessage = gson.fromJson(incomingText, Message.class);
                    processIncomingMessage();
                }
                Thread.sleep(100);
            } catch (SocketTimeoutException e) {
                // do nothing
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkIfAvailableChatsCountUpdated() {
        long availableChatsCount = Application.getAvailableChatsCount();
        if (availableChatsCount != lastAvailableChatsCount) {
            System.out.println("New chats available");
            sendAvailableChatsData();
            this.lastAvailableChatsCount = availableChatsCount;
        }
    }

    private void checkIfActiveChatIsUpdated() {
        try {
            long activeChatHashCode = Application.getUserActiveChatHashCode(userId);
            if (activeChatHashCode != lastActiveChatHashCode) {
                sendChatMessages(Application.getUserActiveChatMessages(userId));
                this.lastActiveChatHashCode = activeChatHashCode;
            }
        } catch (NullPointerException e) {
            //do nothing
        }
    }

    private void processIncomingMessage() {
        switch (incomingMessage.getType()) {
        case LOGIN_USER:
            this.username = incomingMessage.getBody().getMessage();
            Application.newUser(userId, incomingMessage.getBody().getMessage());
            sendUserUUIDData(incomingMessage);
            Application.createChatsCombinationsForNewUser(this);
            sendAvailableChatsData();
            break;
        case NEW_OUTCOMING_MESSAGE:
            processMessageReceived(incomingMessage.getBody());
            break;
        case CHOOSE_CHAT:
            UUID chatId = incomingMessage.getBody().getChatId();
            if (chatId != null) {
                Application.setUserActiveChat(userId, chatId);
                lastActiveChatHashCode = Application.getUserActiveChatHashCode(userId);
                Chat chat = Application.getActiveChatForUser(userId);
                sendChatMessages(chat.getMessages());
            }
            break;
        case LOGOUT_USER:
            processLogoutRequest(incomingMessage.getBody());
            break;
        default:
            break;
        }
    }

    private void processLogoutRequest(MessageBody body) {
        UUID userId = body.getUserId();
        Application.logoutUser(userId);
        loggedOut = true;
    }

    private void processMessageReceived(MessageBody body) {
        UUID chatId = body.getChatId();
        String text = body.getMessage();

        Chat activeChat = Application.getActiveChatForUser(userId);
        if (activeChat.getUuid().equals(chatId)) {
            Application.addMessageToChat(chatId, text);
        } else {
            Application.setUserActiveChat(userId, chatId);
            Application.addMessageToChat(chatId, text);
        }
    }

    private void sendUserUUIDData(Message incomingMessage2) {
        MessageBody body = new MessageBody();
        body.setUserId(userId);
        outcomingMessage.add(new Message(MessageType.LOGIN_USER_ACK, body));
    }

    private void sendAvailableChatsData() {
        MessageBody body = new MessageBody();
        body.setChats(Application.getChatsForUser(userId));
        outcomingMessage.add(new Message(MessageType.AVAILABLE_CHATS, body));
    }

    private void sendChatMessages(List<String> messages) {
        MessageBody body = new MessageBody();
        body.setMessages(messages);
        outcomingMessage.add(new Message(MessageType.NEW_INCOMING_MESSAGE, body));
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
