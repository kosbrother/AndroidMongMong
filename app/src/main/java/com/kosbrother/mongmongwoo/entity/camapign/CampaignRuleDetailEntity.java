package com.kosbrother.mongmongwoo.entity.camapign;

import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.entity.banner.Image;
import com.kosbrother.mongmongwoo.model.Product;

import java.io.Serializable;
import java.util.List;

public class CampaignRuleDetailEntity implements Serializable {

    @SerializedName("image")
    private Image image;
    @SerializedName("items")
    private List<Product> items;

    public Image getImage() {
        return image;
    }

    public List<Product> getItems() {
        return items;
    }
}
