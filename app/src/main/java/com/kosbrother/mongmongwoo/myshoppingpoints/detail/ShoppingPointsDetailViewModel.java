package com.kosbrother.mongmongwoo.myshoppingpoints.detail;

import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsEntity;
import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsDetailEntity;

import java.util.ArrayList;
import java.util.List;

public class ShoppingPointsDetailViewModel {
    private ShoppingPointsDetailEntity shoppingPointsDetailEntity;

    public ShoppingPointsDetailViewModel(ShoppingPointsDetailEntity shoppingPointsDetailEntity) {
        this.shoppingPointsDetailEntity = shoppingPointsDetailEntity;
    }

    public String getAmountText() {
        return "$ " + shoppingPointsDetailEntity.getTotal();
    }

    public List<ShoppingPointsViewModel> getShoppingPointsViewModels() {
        List<ShoppingPointsEntity> shoppingPoints = shoppingPointsDetailEntity.getShoppingPoints();
        List<ShoppingPointsViewModel> shoppingPointsViewModels = new ArrayList<>();
        for (ShoppingPointsEntity entity : shoppingPoints) {
            shoppingPointsViewModels.add(new ShoppingPointsViewModel(entity));
        }
        return shoppingPointsViewModels;
    }
}
