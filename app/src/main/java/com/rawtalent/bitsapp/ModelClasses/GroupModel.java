package com.rawtalent.bitsapp.ModelClasses;

import java.util.List;

public class GroupModel {

    List<String> members;
    List<String> membersNumber;

    String admin;
    String name,description;

    public GroupModel() {
    }

    public List<String> getMembersNumber() {
        return membersNumber;
    }

    public void setMembersNumber(List<String> membersNumber) {
        this.membersNumber = membersNumber;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
