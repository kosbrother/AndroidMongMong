package com.kosbrother.mongmongwoo.myshoppingpoints.campaign;

import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsCampaignsEntity;

import java.util.ArrayList;
import java.util.List;

public class ShoppingPointsCampaignModel {
    private List<ShoppingPointsCampaignsEntity> entities;

    public ShoppingPointsCampaignModel(List<ShoppingPointsCampaignsEntity> entities) {
        this.entities = entities;
    }

    public List<ShoppingPointsCampaignViewModel> getShoppingPointsCampaignViewModels() {
        List<ShoppingPointsCampaignViewModel> list = new ArrayList<>();
        for (ShoppingPointsCampaignsEntity entity : entities) {
            list.add(new ShoppingPointsCampaignViewModel(entity));
        }
        return list;
    }
}
