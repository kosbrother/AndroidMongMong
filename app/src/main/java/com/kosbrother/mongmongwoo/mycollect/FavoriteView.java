package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.model.Product;

import java.util.List;

public interface FavoriteView {
    void showLoadingView();

    void showEmptyView();

    void showMyCollectList(List<Product> myCollectEntities);

    void showProgressDialog();

    void startProductActivity(int productId);

    void hideProgressDialog();

    void showToast(String errorMessage);
}
