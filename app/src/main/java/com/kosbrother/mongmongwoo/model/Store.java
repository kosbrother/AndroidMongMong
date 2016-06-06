package com.kosbrother.mongmongwoo.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Store implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("store_code")
    private String storeCode;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("phone")
    private String phone;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;

    public Store(int id, String storeCode, String name, String address) {
        this.id = id;
        this.storeCode = storeCode;
        this.name = name;
        this.address = address;
    }

    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    public int getId() {
        return id;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

}
