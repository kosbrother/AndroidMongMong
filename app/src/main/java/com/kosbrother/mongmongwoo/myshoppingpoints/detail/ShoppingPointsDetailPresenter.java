package com.kosbrother.mongmongwoo.myshoppingpoints.detail;

import java.util.List;

public class ShoppingPointsDetailPresenter {
    private ShoppingPointsDetailView view;
    private ShoppingPointsDetailModel model;

    public ShoppingPointsDetailPresenter(
            ShoppingPointsDetailView view, ShoppingPointsDetailModel model) {
        this.view = view;
        this.model = model;
    }

    public void onViewCreated() {
        view.setTotalTextView(model.getTotalText());

        List<ShoppingPointsViewModel> shoppingPointsViewModel = model.getShoppingPointsViewModel();
        if (shoppingPointsViewModel.isEmpty()) {
            view.setRecyclerViewEmpty();
        } else {
            view.setRecyclerView(shoppingPointsViewModel);
        }
    }
}
