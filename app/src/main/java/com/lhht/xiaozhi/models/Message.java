package com.lhht.xiaozhi.models;

public class Message {
    private String text;
    private boolean isFromServer;
    private long timestamp;

    public Message(String text, boolean isFromServer) {
        this.text = text;
        this.isFromServer = isFromServer;
        this.timestamp = System.currentTimeMillis();
    }

    public String getText() {
        return text;
    }

    public boolean isFromServer() {
        return isFromServer;
    }

    public long getTimestamp() {
        return timestamp;
    }
} 