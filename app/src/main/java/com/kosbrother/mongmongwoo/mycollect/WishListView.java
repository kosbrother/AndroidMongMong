package com.kosbrother.mongmongwoo.mycollect;

import java.util.List;

interface WishListView {
    void showLoadingView();

    void showWishListView(List<WishListViewModel> wishListViewModels);

    void showWishListEmptyView();

    void startProductActivity(int productId);

    void showProgressDialog();

    void hideProgressDialog();

    void showToast(String errorMessage);
}
