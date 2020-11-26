package com.alaan.roamu.pojo;

import java.io.Serializable;

/**
 * Created by android on 17/3/17.
 */

public class NearbyData implements Serializable {
    String driver_id;
    String travel_id;


    String pickup_address;
    String drop_address;
    String pickup_point;
    String driver_name;
    String driverCity;
    //by ibrahim
    public String driver_mobile;
    public String driverVehicle;
    public String model;
    public String color;
    public String avatar;
    public String vehicle_info;
    public String Travels_Count;
    public String DriverRate;
    public String empty_set;
    //
    String email;
    String travel_time;
    String booked_set;
    String amount;
    String travel_date;
    String avalable_set;
    String somked;
    String pickup_location;
    String drop_location;
    String latitude;
    String longitude;
    //    String vehicle_info;
    String distance;


    public NearbyData() {
    }

    public String getTravel_time() {
        return travel_time;
    }

    public void setTravel_time(String travel_time) {
        this.travel_time = travel_time;
    }

    public String getBooked_set() {
        return booked_set;
    }

    public void setBooked_set(String booked_set) {
        this.booked_set = booked_set;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTravel_date() {
        return travel_date;
    }

    public void setTravel_date(String travel_date) {
        this.travel_date = travel_date;
    }

    public String getAvalable_set() {
        return avalable_set;
    }

    public void setAvalable_set(String avalable_set) {
        this.avalable_set = avalable_set;
    }

    public String getSomked() {
        return somked;
    }

    public void setSomked(String somked) {
        this.somked = somked;
    }

    public String getUser_id() {
        return driver_id;
    }

    //by ibrahim
    public String getTravel_id() {
        return travel_id;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }


    public void setUser_id(String user_id) {
        this.driver_id = user_id;
    }

    //by ibrahim
    public void setTravel_id(String travel_id) {
        this.travel_id = travel_id;
    }

    public String getName() {
        return driver_name;
    }

    public void setName(String name) {
        this.driver_name = name;
    }

    public String getDriverCity() {
        return driverCity;
    }

    public void setDriverCity(String driverCity) {
        this.driverCity = driverCity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getVehicle_info() {
        return model;
    }

    public void setVehicle_info(String model) {
        this.model = model;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public String getPickup_point() {
        return pickup_point;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getDrop_location() {
        return drop_location;
    }

    public void setDrop_location(String drop_location) {
        this.drop_location = drop_location;
    }

}
