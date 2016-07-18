package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostProduct implements Serializable {
    @SerializedName("name")
    private String name;
    @SerializedName("product_id")
    private int productId;
    @SerializedName("spec_id")
    private int specId;
    @SerializedName("style")
    private String style;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("price")
    private int price;

    public void setName(String name) {
        this.name = name;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setSpecId(int specId) {
        this.specId = specId;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
