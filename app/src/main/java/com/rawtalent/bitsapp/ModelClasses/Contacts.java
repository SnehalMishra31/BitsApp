package com.rawtalent.bitsapp.ModelClasses;

public class Contacts {

    long number;
    String chatID;
    String lastMessage;
    long lastMessageDate;
    long notifications;

    public Contacts() {
    }

    public long getNotifications() {
        return notifications;
    }

    public void setNotifications(long notifications) {
        this.notifications = notifications;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}
