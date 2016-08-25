package com.kosbrother.mongmongwoo.entity.user;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MmwUserEntity implements Serializable {

    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String passwrod;
    @SerializedName("registration_id")
    private String registrationId;

    public MmwUserEntity(String email, String passwrod, String registrationId) {
        this.email = email;
        this.passwrod = passwrod;
        this.registrationId = registrationId;
    }
}
