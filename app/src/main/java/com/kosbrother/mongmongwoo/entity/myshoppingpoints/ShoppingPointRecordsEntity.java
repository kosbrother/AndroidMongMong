package com.kosbrother.mongmongwoo.entity.myshoppingpoints;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ShoppingPointRecordsEntity implements Serializable {

    @SerializedName("order_id")
    private Integer orderId;
    @SerializedName("amount")
    private int amount;
    @SerializedName("balance")
    private int balance;
    @SerializedName("created_at")
    private String createdAt;

    public Integer getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }

    public int getBalance() {
        return balance;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
