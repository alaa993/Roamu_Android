package com.alaan.roamu.pojo;

import java.util.Map;

public class firebaseTravel {

    public String driver_id;
    public Map<String,String> clients;

    public firebaseTravel()
    {
    }

    public firebaseTravel(String driver_id, Map<String,String> clients)
    {
        this.driver_id = driver_id;
        this.clients = clients;
    }
}
