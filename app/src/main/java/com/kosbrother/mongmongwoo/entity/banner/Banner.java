package com.kosbrother.mongmongwoo.entity.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Banner {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("bannerable_id")
    @Expose
    public Integer bannerableId;
    @SerializedName("bannerable_type")
    @Expose
    public String bannerableType;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("image")
    @Expose
    public Image image;
    @SerializedName("app_index_url")
    @Expose
    public String appIndexUrl;

    public Image getImage() {
        return image;
    }

    public String getAppIndexUrl() {
        return appIndexUrl;
    }

}