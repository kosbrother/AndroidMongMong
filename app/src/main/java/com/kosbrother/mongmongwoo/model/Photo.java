package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Photo implements Serializable {

    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("photo_intro")
    private String photoIntro;

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPhotoIntro() {
        return photoIntro;
    }

}
