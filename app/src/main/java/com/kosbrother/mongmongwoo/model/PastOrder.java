package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PastOrder implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("status")
    private String status;
    @SerializedName("created_on")
    private String createdOn;
    @SerializedName("items_price")
    private int itemPrice;
    @SerializedName("ship_fee")
    private int shipFee;
    @SerializedName("total")
    private int total;
    @SerializedName("note")
    private String note;
    @SerializedName("info")
    private Info info;
    @SerializedName("items")
    private List<PastOrderProduct> items;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public int getShipFee() {
        return shipFee;
    }

    public int getTotal() {
        return total;
    }

    public List<PastOrderProduct> getItems() {
        return items;
    }

    public String getNote() {
        return note;
    }

    public String getShipName() {
        return info.getShipName();
    }

    public String getShipPhone() {
        return info.getShipPhone();
    }

    public String getShipStoreName() {
        return info.getShipStoreName();
    }

    private class Info {
        @SerializedName("id")
        private int id;
        @SerializedName("ship_name")
        private String shipName;
        @SerializedName("ship_phone")
        private String shipPhone;
        @SerializedName("ship_store_code")
        private String shipStoreCode;
        @SerializedName("ship_store_id")
        private String shipStoreId;
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

        public String getShipStoreId() {
            return shipStoreId;
        }

        public String getShipStoreName() {
            return shipStoreName;
        }
    }
}
