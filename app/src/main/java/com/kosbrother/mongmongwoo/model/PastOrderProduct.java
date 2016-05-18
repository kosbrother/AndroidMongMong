package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

public class PastOrderProduct {

    @SerializedName("name")
    private String name;
    @SerializedName("style")
    private String style;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("price")
    private int price;

    public String getName() {
        return name;
    }

    public String getStyle() {
        return style;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

}
