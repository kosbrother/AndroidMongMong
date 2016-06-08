package com.kosbrother.mongmongwoo.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_name")
    private String userName;
    @SerializedName("real_name")
    private String real_name;
    @SerializedName("gender")
    private String gender;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private String address;
    @SerializedName("uid")
    private String fb_uid;
    @SerializedName("email")
    private String email;

    private String fb_pic;

    public User(String userName, String real_name, String gender, String phone, String address, String fb_uid, String fb_pic, String email) {
        this.userName = userName;
        this.real_name = real_name;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
        this.fb_uid = fb_uid;
        this.fb_pic = fb_pic;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public String getReal_name() {
        return real_name;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getFb_uid() {
        return fb_uid;
    }

    public String getFb_pic() {
        return fb_pic;
    }

    public String getEmail() {
        return email;
    }

    public String getJsonString() {
        return new Gson().toJson(this, User.class);
    }
}
