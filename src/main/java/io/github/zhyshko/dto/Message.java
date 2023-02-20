package io.github.zhyshko.dto;

public class Message {

    private MessageType type;
    private MessageBody body;

    public Message(MessageType type, MessageBody body) {
        this.type = type;
        this.body = body;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageBody getBody() {
        return body;
    }

    public void setBody(MessageBody body) {
        this.body = body;
    }

}
