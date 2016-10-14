package com.kosbrother.mongmongwoo.entity.camapign;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.entity.banner.Image;

import java.io.Serializable;

public class CampaignRuleEntity implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("valid_until")
    @Expose
    private String validUntil;
    @SerializedName("app_index_url")
    @Expose
    private String appIndexUrl;
    @SerializedName("icon")
    @Expose
    private Image icon;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public String getAppIndexUrl() {
        return appIndexUrl;
    }

    public Image getIcon() {
        return icon;
    }
}
