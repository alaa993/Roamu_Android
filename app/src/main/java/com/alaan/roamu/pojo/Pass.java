package com.alaan.roamu.pojo;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;

import java.io.Serializable;

/**
 * Created by android on 13/10/17.
 */

public class Pass implements Serializable, Parcelable {



    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    private String fromPlace;
    private String toPlace;
    private String fromAddress;
    private String toAddress;
    private String Check;

    private String driverId;
    // by ibrahim
    private String travelId;
//    public String model;
    public String color;
    public String avatar;
    public String vehicle_info;
    public String Travels_Count;
    public String DriverRate;
    public String empty_set;
    public String pickup_location;
    static public enum fragment_type {
        ADD,
        GET
    }
    public fragment_type f;
    //
    private String driverName;
    private String date;
    private String smoke;
    private String status;
    private String avalibleset;

    public int NoPassengers;
    public int TripPrice;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getcheck() {
        return Check;
    }

    public void setCheck(String Check) {
        this.Check = Check;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvalibleset() {
        return avalibleset;
    }

    public void setAvalibleset(String avalibleset) {
        this.avalibleset = avalibleset;
    }

    private String fare;
    private String model;

    public String getVehicleName() {
        return model;
    }

    public void setVehicleName(String vehicleName) {
        this.model = vehicleName;
    }

    public Pass() {
    }

    protected Pass(Parcel in) {
        driverId = in.readString();
        travelId = in.readString();
        driverName = in.readString();
        fare = in.readString();
    }

    public static final Creator<Pass> CREATOR = new Creator<Pass>() {
        @Override
        public Pass createFromParcel(Parcel in) {
            return new Pass(in);
        }

        @Override
        public Pass[] newArray(int size) {
            return new Pass[size];
        }
    };

    public String getFromPlace() {

        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getTravelId() {
        return travelId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public void setTravelId(String travelId) {
        this.travelId = travelId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(driverId);
        parcel.writeString(travelId);
        parcel.writeString(driverName);
        parcel.writeString(fare);
    }
}
