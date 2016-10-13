package com.kosbrother.mongmongwoo.entity.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.checkout.ActivityCampaignViewModel;
import com.kosbrother.mongmongwoo.checkout.ShoppingPointCampaignViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderPrice implements Serializable {

    @SerializedName("origin_items_price")
    @Expose
    public int originItemsPrice;
    @SerializedName("shopping_point")
    @Expose
    public ShoppingPoint shoppingPoint;
    @SerializedName("campaigns")
    @Expose
    public List<Campaign> campaigns = new ArrayList<Campaign>();
    @SerializedName("obtain_shopping_point_amount")
    @Expose
    public int obtainShoppingPointAmount;
    @SerializedName("shopping_point_campaigns")
    @Expose
    public List<ShoppingPointCampaign> shoppingPointCampaigns = new ArrayList<ShoppingPointCampaign>();
    @SerializedName("ship_fee")
    @Expose
    public int shipFee;
    @SerializedName("ship_campaign")
    @Expose
    public ShipCampaign shipCampaign;
    @SerializedName("total")
    @Expose
    public int total;

    public int getOriginItemsPrice() {
        return originItemsPrice;
    }

    public ShoppingPoint getShoppingPoint() {
        return shoppingPoint;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }

    public List<ShoppingPointCampaign> getShoppingPointCampaigns() {
        return shoppingPointCampaigns;
    }

    public int getObtainShoppingPointAmount() {
        return obtainShoppingPointAmount;
    }

    public int getShipFee() {
        return shipFee;
    }

    public ShipCampaign getShipCampaign() {
        return shipCampaign;
    }

    public int getTotal() {
        return total;
    }

    public List<ActivityCampaignViewModel> getActivityCampaignViewModels() {
        List<ActivityCampaignViewModel> viewModelList = new ArrayList<>();
        for (Campaign campaign : campaigns) {
            viewModelList.add(new ActivityCampaignViewModel(campaign, true));
        }
        return viewModelList;
    }

    public List<ShoppingPointCampaignViewModel> getShoppingPointCampaignViewModels() {
        List<ShoppingPointCampaignViewModel> viewModelList = new ArrayList<>();
        for (ShoppingPointCampaign campaign : shoppingPointCampaigns) {
            viewModelList.add(new ShoppingPointCampaignViewModel(campaign, true));
        }
        return viewModelList;
    }
}
