package com.alaan.roamu.pojo;

public class firebaseRide {
    public String id;
    public String payment_mode;
    public String payment_status;
    public String ride_status;
    public String travel_status;
    public Long timestamp;

    public firebaseRide()
    {
    }

    public firebaseRide(String id, String payment_mode,
                        String payment_status, String ride_status,
                        String travel_status, Long timestamp)
    {
        this.id = id;
        this.payment_mode = payment_mode;
        this.payment_status = payment_status;
        this.ride_status = ride_status;
        this.travel_status = travel_status;
        this.timestamp = timestamp;
    }
}
