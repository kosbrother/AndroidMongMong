package com.kosbrother.mongmongwoo.entity.pastorder;

import com.google.gson.annotations.SerializedName;

class InfoEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("ship_name")
    private String shipName;
    @SerializedName("ship_phone")
    private String shipPhone;
    @SerializedName("ship_email")
    private String shipEmail;
    @SerializedName("ship_store_code")
    private String shipStoreCode;
    @SerializedName("ship_store_id")
    private int shipStoreId;
    @SerializedName("ship_store_name")
    private String shipStoreName;
    @SerializedName("ship_store_address")
    private String shipStoreAddress;
    @SerializedName("ship_store_phone")
    private String shipStorePhone;

    public int getId() {
        return id;
    }

    public String getShipName() {
        return shipName;
    }

    public String getShipPhone() {
        return shipPhone;
    }

    public String getShipEmail() {
        return shipEmail;
    }

    public String getShipStoreName() {
        return shipStoreName;
    }

    public String getShipStoreAddress() {
        return shipStoreAddress;
    }

    public String getShipStorePhone() {
        return shipStorePhone;
    }
}
