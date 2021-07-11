package com.rawtalent.bitsapp.GroupInfo;

import android.graphics.Bitmap;

public class MemberModel {

    String name,uid;
    Bitmap image;

    public MemberModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
