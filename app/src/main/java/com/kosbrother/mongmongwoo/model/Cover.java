package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cover implements Serializable {
    @SerializedName("url")
    private String url;
    @SerializedName("icon")
    private Icon icon;

    public Cover(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    private class Icon implements Serializable{
        @SerializedName("url")
        private String url;
    }
}
