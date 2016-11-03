package com.kosbrother.mongmongwoo.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GetNewAppEntity implements Serializable {
    @SerializedName("is_ready")
    private boolean isReady;
    @SerializedName("url")
    private String url;
    @SerializedName("coupon")
    private String coupon;

    public boolean isReady() {
        return isReady;
    }

    public String getUrl() {
        return url;
    }

    public String getCoupon() {
        return coupon;
    }
}
