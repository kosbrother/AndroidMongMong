package com.kosbrother.mongmongwoo.entity.pastorder;

import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.entity.checkout.Campaign;
import com.kosbrother.mongmongwoo.entity.checkout.ShipCampaign;
import com.kosbrother.mongmongwoo.entity.checkout.ShoppingPointCampaign;

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
    @SerializedName("ship_type")
    private String shipType;
    @SerializedName("campaigns")
    private List<Campaign> campaigns;
    @SerializedName("shopping_point_campaigns")
    private List<ShoppingPointCampaign> shoppingPointCampaigns;
    @SerializedName("ship_campaign")
    private ShipCampaign shipCampaign;
    @SerializedName("info")
    private InfoEntity info;
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

    public InfoEntity getInfo() {
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

    public String getShipType() {
        return shipType;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public List<ShoppingPointCampaign> getShoppingPointCampaigns() {
        return shoppingPointCampaigns;
    }

    public ShipCampaign getShipCampaign() {
        return shipCampaign;
    }
}
