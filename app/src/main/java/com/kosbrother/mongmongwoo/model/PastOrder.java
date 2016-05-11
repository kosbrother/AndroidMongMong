package com.kosbrother.mongmongwoo.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kolichung on 3/22/16.
 */
public class PastOrder implements Serializable {

    ArrayList<PastOrderProduct> pastOrderProducts;
    String shipName;
    String shipPhone;
    Store shippingStore;
    int shipFee;
    int itemPrice;
    String note;
//    "id":18,
//    "user_id":26,
//    "total":110,
//    "created_at":"2016-03-18T05:14:33.000Z",
//    "status":"order_placed",
//    "uid":"1153503537995545"

    public PastOrder(int order_id, int total, String date, String status) {
        this.order_id = order_id;
        this.total = total;
        this.date = date;
        this.status = status;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    int order_id;

    public void setTotalPrice(int total_price) {
        this.total = total_price;
    }

    int total;

    public void setDate(String date) {
        this.date = date;
    }

    String date;

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public void setPastOrderProducts(ArrayList<PastOrderProduct> pastOrderProducts) {
        this.pastOrderProducts = pastOrderProducts;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public void setShipPhone(String shipPhone) {
        this.shipPhone = shipPhone;
    }

    public void setShippingStore(Store shippingStore) {
        this.shippingStore = shippingStore;
    }

    public void setShipFee(int shipFee) {
        this.shipFee = shipFee;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public ArrayList<PastOrderProduct> getPastOrderProducts() {
        return pastOrderProducts;
    }

    public String getShipName() {
        return shipName;
    }

    public String getShipPhone() {
        return shipPhone;
    }

    public Store getShippingStore() {
        return shippingStore;
    }

    public int getShipFee() {
        return shipFee;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
