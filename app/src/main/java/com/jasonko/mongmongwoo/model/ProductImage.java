package com.jasonko.mongmongwoo.model;

/**
 * Created by kolichung on 3/17/16.
 */
public class ProductImage {

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
