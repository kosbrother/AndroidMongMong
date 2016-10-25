package com.kosbrother.mongmongwoo.entity.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShipCampaign {
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("left_to_apply")
    @Expose
    public int leftToApply;

    public String getTitle() {
        return title;
    }

    public int getLeftToApply() {
        return leftToApply;
    }
}
