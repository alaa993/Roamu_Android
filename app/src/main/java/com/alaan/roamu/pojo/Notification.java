package com.alaan.roamu.pojo;

import java.io.Serializable;

public class Notification implements Serializable {
    public String id;
    public String text;
    public String uid;
    public String ride_id;
    public String readStatus;
    public Long timestamp;


    public Notification()
    {
    }

    public Notification(String id, String text, String uid, String ride_id, String readStatus, Long timestamp)
    {
        this.id = id;
        this.text = text;
        this.uid = uid;
        this.ride_id = ride_id;
        this.readStatus = readStatus;
        this.timestamp = timestamp;
    }
}
