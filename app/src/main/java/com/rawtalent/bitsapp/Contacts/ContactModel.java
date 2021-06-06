package com.rawtalent.bitsapp.Contacts;

public class ContactModel {

    String number,name;
    String uuid;
    boolean checked;

    public ContactModel() {
        checked=false;
    }



    public ContactModel(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
