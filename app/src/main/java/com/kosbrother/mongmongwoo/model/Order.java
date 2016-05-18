package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Order {

    @SerializedName("uid")
    String uid;
    @SerializedName("items_price")
    int productPrice;
    @SerializedName("ship_fee")
    int shipPrice;
    @SerializedName("total")
    int totalPrice;
    @SerializedName("ship_name")
    String shippingName;
    @SerializedName("ship_phone")
    String shippingPhone;
    @SerializedName("ship_email")
    String shippingEmail;
    @SerializedName("products")
    List<Product> orderProducts;
    @SerializedName("registration_id")
    private String registrationId;
    @SerializedName("ship_store_code")
    private String shipStoreCode;
    @SerializedName("ship_store_id")
    private int shipStoreId;
    @SerializedName("ship_store_name")
    private String shipStoreName;
    private Store shippingStore;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getShippingName() {
        return shippingName;
    }

    public String getShippingPhone() {
        return shippingPhone;
    }

    public Store getShippingStore() {
        return shippingStore;
    }

    public List<Product> getOrderProducts() {
        if (orderProducts == null) {
            orderProducts = new ArrayList<>();
        }
        return orderProducts;
    }

    public int getShipPrice() {
        return shipPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    public void setShippingStore(Store shippingStore) {
        this.shippingStore = shippingStore;
        shipStoreName = shippingStore.getName();
        shipStoreCode = shippingStore.getStoreCode();
        shipStoreId = shippingStore.getId();
    }

    public void setOrderProducts(List<Product> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public void setShipPrice(int shipPrice) {
        this.shipPrice = shipPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setShippingEmail(String shippingEmail) {
        this.shippingEmail = shippingEmail;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
