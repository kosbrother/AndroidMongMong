package com.kosbrother.mongmongwoo.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kolichung on 3/22/16.
 */
public class PastOrder implements Serializable {

    ArrayList<PastOrderProduct> pastOrderProducts;
    String shippingName;
    String shippingPhone;
    Store shippingStore;
    int shipPrice;
    int productPrice;
    String note;
//    "id":18,
//    "user_id":26,
//    "total":110,
//    "created_at":"2016-03-18T05:14:33.000Z",
//    "status":"order_placed",
//    "uid":"1153503537995545"

    public PastOrder(int order_id, int total_price, String date, String status) {
        this.order_id = order_id;
        this.total_price = total_price;
        this.date = date;
        this.status = status;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getTotal_price() {
        return total_price;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    int order_id;
    int total_price;
    String date;
    String status;

    public void setPastOrderProducts(ArrayList<PastOrderProduct> pastOrderProducts) {
        this.pastOrderProducts = pastOrderProducts;
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

    public void setShipPrice(int shipPrice) {
        this.shipPrice = shipPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public ArrayList<PastOrderProduct> getPastOrderProducts() {
        return pastOrderProducts;
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

    public int getShipPrice() {
        return shipPrice;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
