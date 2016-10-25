package com.kosbrother.mongmongwoo.entity.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShoppingPointCampaign {

    @SerializedName("is_applied")
    @Expose
    private boolean isApplied;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("left_to_apply")
    @Expose
    private int leftToApply;

    public boolean isApplied() {
        return isApplied;
    }

    public String getTitle() {
        return title;
    }

    public int getLeftToApply() {
        return leftToApply;
    }
}
