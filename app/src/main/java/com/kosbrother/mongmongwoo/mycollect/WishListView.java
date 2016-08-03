package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;

import java.util.List;

public interface WishListView {
    void showLoadingView();

    void showWishListView(List<Product> wishListProducts, List<Spec> specs);

    void showWishListEmptyView();

    void startProductActivity(int productId);

    void showProgressDialog();

    void hideProgressDialog();

    void showToast(String errorMessage);
}
