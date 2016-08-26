package com.kosbrother.mongmongwoo.myshoppingpoints.campaign;

public class ShoppingPointsCampaignPresenter {
    private ShoppingPointsCampaignView view;
    private ShoppingPointsCampaignModel model;

    public ShoppingPointsCampaignPresenter(ShoppingPointsCampaignView view, ShoppingPointsCampaignModel model) {
        this.view = view;
        this.model = model;
    }

    public void onViewCreated() {
        view.setRecyclerView(model.getShoppingPointsCampaignViewModels());
    }
}
