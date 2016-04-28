package com.kosbrother.mongmongwoo.model;

import java.io.Serializable;

public class ProductImage implements Serializable {

    public String getIntro() {
        return intro;
    }

    public ProductImage(String url, String thumb, String medium, String intro) {
        this.url = url;
        this.thumb = thumb;
        this.medium = medium;
        this.intro = intro;
    }

    public String getUrl() {
        return url;
    }

    public String getThumb() {
        return thumb;
    }

    public String getMedium() {
        return medium;
    }

    String url;
    String thumb;
    String medium;
    String intro;


}
