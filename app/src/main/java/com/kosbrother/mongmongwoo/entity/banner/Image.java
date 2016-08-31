package com.kosbrother.mongmongwoo.entity.banner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("url")
    @Expose
    public String url;

    public String getUrl() {
        return url;
    }

}
