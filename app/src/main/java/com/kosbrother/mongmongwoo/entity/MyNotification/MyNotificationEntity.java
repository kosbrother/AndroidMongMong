package com.kosbrother.mongmongwoo.entity.MyNotification;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MyNotificationEntity implements Serializable{

    @SerializedName("id")
    private int id;
    @SerializedName("message_type")
    private String messageType;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("app_index_url")
    private String appIndexUrl;

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getAppIndexUrl() {
        return appIndexUrl;
    }
}
