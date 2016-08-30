package com.kosbrother.mongmongwoo.entity.pastorder;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

class PastOrderEntity implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("uid")
    private String uid;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("status")
    private String status;
    @SerializedName("created_on")
    private String createdOn;
    @SerializedName("items_price")
    private int itemsPrice;
    @SerializedName("ship_fee")
    private int shipFee;
    @SerializedName("total")
    private int total;
    @SerializedName("shopping_point_amount")
    private int shoppingPointAmount;
    @SerializedName("note")
    private String note;
    @SerializedName("info")
    private Info info;
    @SerializedName("items")
    private List<PastItem> items;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public int getItemsPrice() {
        return itemsPrice;
    }

    public int getTotal() {
        return total;
    }

    public int getShoppingPointAmount() {
        return shoppingPointAmount;
    }

    public Info getInfo() {
        return info;
    }

    public List<PastItem> getItems() {
        return items;
    }

    public String getNote() {
        return note;
    }

    public int getShipFee() {
        return shipFee;
    }
}
