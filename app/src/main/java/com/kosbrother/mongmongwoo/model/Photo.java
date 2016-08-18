package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Photo implements Serializable {

    @SerializedName("image")
    private Image image;

    public Image getImage() {
        return image;
    }

    public class Image implements Serializable {
        @SerializedName("url")
        private String url;
        @SerializedName("thumb")
        private Thumb photoIntro;
        @SerializedName("cover")
        private Cover cover;

        public String getUrl() {
            return url;
        }

        private class Thumb implements Serializable {
            @SerializedName("url")
            private String url;
        }
    }
}
