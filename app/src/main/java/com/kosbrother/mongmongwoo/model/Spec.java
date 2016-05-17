package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Spec implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("style")
    private String style;
    @SerializedName("amount")
    private int amount;
    @SerializedName("pic")
    private String pic;

    public Spec(int id, String style, int amount, String pic) {
        this.id = id;
        this.style = style;
        this.amount = amount;
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public String getStyle() {
        return style;
    }

    public int getAmount() {
        return amount;
    }

    public String getPic() {
        return pic;
    }

}
