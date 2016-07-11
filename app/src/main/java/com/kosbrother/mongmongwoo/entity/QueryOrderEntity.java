package com.kosbrother.mongmongwoo.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class QueryOrderEntity implements Serializable {
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
    @SerializedName("note")
    private String note;
    @SerializedName("info")
    private Info info;
    @SerializedName("items")
    private List<PastItemEntity> items;

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

    public List<PastItemEntity> getItems() {
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
        @SerializedName("ship_email")
        private String shipEmail;
        @SerializedName("ship_store_code")
        private String shipStoreCode;
        @SerializedName("ship_store_id")
        private int shipStoreId;
        @SerializedName("ship_store_name")
        private String shipStoreName;
        @SerializedName("ship_store_address")
        private String shipStoreAddress;
        @SerializedName("ship_store_phone")
        private String shipStorePhone;

        public int getId() {
            return id;
        }

        public String getShipName() {
            return shipName;
        }

        public String getShipPhone() {
            return shipPhone;
        }

        public String getShipEmail() {
            return shipEmail;
        }

        public String getShipStoreName() {
            return shipStoreName;
        }

        public String getShipStoreAddress() {
            return shipStoreAddress;
        }

        public String getShipStorePhone() {
            return shipStorePhone;
        }
    }

    public static class PastItemEntity {
        @SerializedName("name")
        private String name;
        @SerializedName("style")
        private String style;
        @SerializedName("quantity")
        private int quantity;
        @SerializedName("price")
        private int price;
        @SerializedName("style_pic")
        private String stylePic;

        public String getName() {
            return name;
        }

        public String getStyle() {
            return style;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getPrice() {
            return price;
        }

        public String getStylePic() {
            return stylePic;
        }
    }
}
