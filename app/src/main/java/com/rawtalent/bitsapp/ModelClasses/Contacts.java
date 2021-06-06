package com.rawtalent.bitsapp.ModelClasses;

public class Contacts {

    String number;
    String chatID;
    String lastMessage;
    long lastMessageDate;
    long notifications;

    long blocked;
    String stringDate,stringTime;
    String publicKey;
    String sender;

    public Contacts() {
    }

    public Contacts(String number, String chatID, String lastMessage, long lastMessageDate, long notifications, long blocked, String stringDate, String stringTime, String publicKey, String sender) {
        this.number = number;
        this.chatID = chatID;
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
        this.notifications = notifications;
        this.blocked = blocked;
        this.stringDate = stringDate;
        this.stringTime = stringTime;
        this.publicKey = publicKey;
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getBlocked() {
        return blocked;
    }

    public void setBlocked(long blocked) {
        this.blocked = blocked;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public long getNotifications() {
        return notifications;
    }

    public void setNotifications(long notifications) {
        this.notifications = notifications;
    }

    public String getNumber() {
        return number;
    }

    public String getStringDate() {
        return stringDate;
    }

    public void setStringDate(String stringDate) {
        this.stringDate = stringDate;
    }

    public String getStringTime() {
        return stringTime;
    }

    public void setStringTime(String stringTime) {
        this.stringTime = stringTime;
    }

    public void setNumber(String number) {
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
