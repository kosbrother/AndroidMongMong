package com.kosbrother.mongmongwoo.mycollect;

import java.util.List;

interface FavoriteView {
    void showLoadingView();

    void showEmptyView();

    void showMyCollectList(List<FavoriteViewModel> favoriteViewModels);

    void showProgressDialog();

    void startProductActivity(int productId);

    void hideProgressDialog();

    void showToast(String errorMessage);
}
