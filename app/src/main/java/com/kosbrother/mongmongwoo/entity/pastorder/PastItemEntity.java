package com.kosbrother.mongmongwoo.entity.pastorder;

import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.entity.checkout.Campaign;

import java.util.List;

class PastItemEntity {
    @SerializedName("name")
    private String name;
    @SerializedName("style")
    private String style;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("price")
    private int price;
    @SerializedName("subtotal")
    private int subtotal;
    @SerializedName("style_pic")
    private String stylePic;
    @SerializedName("campaigns")
    private List<Campaign> campaigns;

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

    public int getSubtotal() {
        return subtotal;
    }

    public String getStylePic() {
        return stylePic;
    }

    public List<Campaign> getCampaigns() {
        return campaigns;
    }
}
