package com.kosbrother.mongmongwoo.entity;

import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.model.PostProduct;

import java.io.Serializable;
import java.util.List;

public class PastOrderEntity implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("items_price")
    private int itemsPrice;
    @SerializedName("note")
    private String note;
    @SerializedName("info")
    private Info info;
    @SerializedName("items")
    private List<PostProduct> items;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("ship_fee")
    private int shipFee;
    @SerializedName("total")
    private int total;
    @SerializedName("status")
    private String status;
    @SerializedName("uid")
    private String uid;
    @SerializedName("created_on")
    private String createdOn;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public int getTotal() {
        return total;
    }

    public Info getInfo() {
        return info;
    }

    public List<PostProduct> getItems() {
        return items;
    }

    public String getNote() {
        return note;
    }

    public int getShipFee() {
        return shipFee;
    }

    public static class Info {
        @SerializedName("id")
        private int id;
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

        public int getId() {
            return id;
        }

        public String getShipName() {
            return shipName;
        }

        public String getShipPhone() {
            return shipPhone;
        }

        public String getShipStoreCode() {
            return shipStoreCode;
        }

        public int getShipStoreId() {
            return shipStoreId;
        }

        public String getShipStoreName() {
            return shipStoreName;
        }
    }
}
