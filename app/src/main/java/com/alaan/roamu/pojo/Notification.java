package com.alaan.roamu.pojo;

import java.io.Serializable;

public class Notification implements Serializable {
    public String id;
    public String text;
    public String uid;
    public String ride_id;
    public String travel_id;
    public String readStatus;
    public Long timestamp;


    public Notification() {
        this.id = "";
        this.text = "";
        this.uid = "";
        this.ride_id = "";
        this.travel_id = "";
        this.readStatus = "";
        this.timestamp = Long.valueOf(0);
    }

    public Notification(String id, String text, String uid, String ride_id, String travel_id, String readStatus, Long timestamp)
    {
        this.id = id;
        this.text = text;
        this.uid = uid;
        this.ride_id = ride_id;
        this.travel_id = travel_id;
        this.readStatus = readStatus;
        this.timestamp = timestamp;
    }
}
