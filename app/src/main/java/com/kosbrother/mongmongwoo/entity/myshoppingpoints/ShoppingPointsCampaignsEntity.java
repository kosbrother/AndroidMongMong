package com.kosbrother.mongmongwoo.entity.myshoppingpoints;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShoppingPointsCampaignsEntity implements Serializable {

    @SerializedName("id")
    private Integer id;
    @SerializedName("description")
    private String description;
    @SerializedName("amount")
    private Integer amount;
    @SerializedName("valid_until")
    private String validUntil;
    @SerializedName("is_expired")
    private Boolean isExpired;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("title")
    private String title;
    @SerializedName("is_collected")
    private Boolean isCollected;

    public String getDescription() {
        return description;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getCollected() {
        return isCollected;
    }
}
