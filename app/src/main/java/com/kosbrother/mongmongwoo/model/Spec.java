package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Spec implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("style")
    private String style;
    @SerializedName("style_pic")
    private StylePic stylePic;

    public Spec(int id, String style, StylePic stylePic) {
        this.id = id;
        this.style = style;
        this.stylePic = stylePic;
    }

    public int getId() {
        return id;
    }

    public String getStyle() {
        return style;
    }

    public StylePic getStylePic() {
        return stylePic;
    }

    public static class StylePic implements Serializable{
        @SerializedName("url")
        private String url;

        public StylePic(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
