package com.kosbrother.mongmongwoo.entity;

import com.google.gson.annotations.SerializedName;

public class AndroidVersionEntity {
    @SerializedName("version_name")
    private String versionName;

    @SerializedName("version_code")
    private int versionCode;

    @SerializedName("update_message")
    private String updateMessage;

    public int getVersionCode() {
        return versionCode;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public String getVersionName() {
        return versionName;
    }
}
