package com.rawtalent.bitsapp.ModelClasses;

public class GroupListModel {

    String lastMessage;
    long lastMessageDate;
    String name;
    String admin;

    long notifications;
    String stringDate,stringTime;
    String publicKey;
    String privateKey;

    public GroupListModel() {
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNotifications() {
        return notifications;
    }

    public void setNotifications(long notifications) {
        this.notifications = notifications;
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
