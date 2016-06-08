package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

public class PostProduct {
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

    public int getQuantity() {
        return quantity;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}
