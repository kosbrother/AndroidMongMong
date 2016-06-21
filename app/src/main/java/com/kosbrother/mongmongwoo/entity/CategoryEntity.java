package com.kosbrother.mongmongwoo.entity;

import com.google.gson.annotations.SerializedName;

public class CategoryEntity {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("image")
    protected Image image;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public class Image {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
