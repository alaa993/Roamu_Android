package com.alaan.roamu.pojo;

public class Fav_Places {
    public String id;
    public String uid;
    public Double latitude;
    public Double longitude;
    public String name;
    public String type;


    public Fav_Places() {
    }

    public Fav_Places(String uid, Double latitude, Double longitude, String name, String type) {
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.type = type;
    }
}