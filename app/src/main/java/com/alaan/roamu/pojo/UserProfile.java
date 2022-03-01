package com.alaan.roamu.pojo;

import java.io.Serializable;

public class UserProfile implements Serializable {
    public String uid;
    public String username;
    public String photoURL;

    public UserProfile() {
        this.uid = "";
        this.username = "";
        this.photoURL = "";
    }
}