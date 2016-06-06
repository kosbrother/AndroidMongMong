package com.kosbrother.mongmongwoo.api;

import com.google.gson.annotations.SerializedName;

public class Cover {
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

    private class Icon {
        @SerializedName("url")
        private String url;
    }
}
