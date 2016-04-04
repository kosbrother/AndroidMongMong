package com.kosbrother.mongmongwoo.model;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/14/16.
 */
public class Order {

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        if (uid == null){
            return "9999"; //匿名購買
        }else {
            return uid;
        }
    }

    String uid;


    public Order() {
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

    public ArrayList<Product> getOrderProducts() {
        if (orderProducts == null){
            orderProducts = new ArrayList<>();
        }
        return orderProducts;
    }

    public int getShipPrice() {
        return shipPrice;
    }

    public int getProductPrice() {
        return productPrice;
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
    }

    public void setOrderProducts(ArrayList<Product> orderProducts) {
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

    String shippingName;
    String shippingPhone;
    Store shippingStore;
    ArrayList<Product> orderProducts;
    int shipPrice;
    int productPrice;
    int totalPrice;


}
