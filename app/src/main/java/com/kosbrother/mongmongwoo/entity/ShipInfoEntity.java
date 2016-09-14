package com.kosbrother.mongmongwoo.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShipInfoEntity implements Serializable{
    @SerializedName("user_id")
    private int userId;
    @SerializedName("ship_email")
    private String shipEmail;
    @SerializedName("ship_phone")
    private String shipPhone;

    public ShipInfoEntity(int userId, String shipEmail, String shipPhone) {
        this.userId = userId;
        this.shipEmail = shipEmail;
        this.shipPhone = shipPhone;
    }
}
