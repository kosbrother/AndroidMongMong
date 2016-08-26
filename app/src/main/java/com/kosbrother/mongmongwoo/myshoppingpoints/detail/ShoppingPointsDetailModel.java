package com.kosbrother.mongmongwoo.myshoppingpoints.detail;

import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsDetailEntity;

import java.util.List;

public class ShoppingPointsDetailModel {

    private final ShoppingPointsDetailViewModel viewModel;

    public ShoppingPointsDetailModel(ShoppingPointsDetailEntity shoppingPointsEntity) {
        viewModel = new ShoppingPointsDetailViewModel(shoppingPointsEntity);
    }

    public String getTotalText() {
        return ""+viewModel.getAmountText();
    }

    public List<ShoppingPointsViewModel> getShoppingPointsViewModel() {
        return viewModel.getShoppingPointsViewModels();
    }
}
