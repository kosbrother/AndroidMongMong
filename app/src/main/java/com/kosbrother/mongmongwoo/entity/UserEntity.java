package com.kosbrother.mongmongwoo.entity;

import com.google.gson.annotations.SerializedName;

public class UserEntity {
    @SerializedName("email")
    protected String email;
    @SerializedName("provider")
    protected String provider;
    @SerializedName("uid")
    protected String uid;
    @SerializedName("user_name")
    protected String userName;
    @SerializedName("gender")
    protected String gender;

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
