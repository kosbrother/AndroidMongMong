package com.jasonko.mongmongwoo.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by kolichung on 3/14/16.
 */
public class Store  implements Serializable {

    int store_id;
    String store_code;
    String name;
    String address;
    double lat;
    double lng;

    public Store(int store_id, String store_code, String name, String address) {
        this.store_id = store_id;
        this.store_code = store_code;
        this.name = name;
        this.address = address;
    }

    public void setLatLng(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    public LatLng getLatLng(){
        return new LatLng(lat,lng);
    }

    public int getStore_id() {
        return store_id;
    }

    public String getStore_code() {
        return store_code;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

}
