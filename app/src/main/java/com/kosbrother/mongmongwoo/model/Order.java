package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {

    @SerializedName("email")
    private String email;
    @SerializedName("items_price")
    private int itemsPrice;
    @SerializedName("ship_fee")
    private int shipFee;
    @SerializedName("total")
    private int total;
    @SerializedName("registration_id")
    private String registrationId;
    @SerializedName("ship_name")
    private String shipName;
    @SerializedName("ship_phone")
    private String shipPhone;
    @SerializedName("ship_store_code")
    private String shipStoreCode;
    @SerializedName("ship_store_id")
    private int shipStoreId;
    @SerializedName("ship_store_name")
    private String shipStoreName;
    @SerializedName("ship_email")
    private String shipEmail;
    @SerializedName("products")
    private List<PostProduct> products;

    public int getShipFee() {
        return shipFee;
    }

    public int getTotal() {
        return total;
    }

    public String getShipEmail() {
        return shipEmail;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public void setShipPhone(String shipPhone) {
        this.shipPhone = shipPhone;
    }

    public void setProducts(List<PostProduct> products) {
        this.products = products;
    }

    public void setShipFee(int shipFee) {
        this.shipFee = shipFee;
    }

    public void setItemsPrice(int itemsPrice) {
        this.itemsPrice = itemsPrice;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setShipEmail(String shipEmail) {
        this.shipEmail = shipEmail;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public void setShipStoreCode(String shipStoreCode) {
        this.shipStoreCode = shipStoreCode;
    }

    public void setShipStoreId(int shipStoreId) {
        this.shipStoreId = shipStoreId;
    }

    public void setShipStoreName(String shipStoreName) {
        this.shipStoreName = shipStoreName;
    }

    public int getItemsPrice() {
        return itemsPrice;
    }

}
