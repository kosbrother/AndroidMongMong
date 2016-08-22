package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.mycollect.WishListEntity;

import java.util.List;

public class WishListPresenter implements DataManager.ApiCallBack {
    private WishListView view;
    private WishListModel model;

    public WishListPresenter(WishListView view, WishListModel model) {
        this.view = view;
        this.model = model;
    }

    public void onResume() {
        view.showLoadingView();
        model.getWishList(this);
    }

    public void onDestroy() {
        view.hideProgressDialog();
        model.cancelRequest(this);
    }

    @Override
    public void onError(String errorMessage) {
        view.hideProgressDialog();
        view.showToast(errorMessage);
    }

    @Override
    public void onSuccess(Object data) {
        view.hideProgressDialog();
        if (data instanceof List) {
            List<WishListEntity> wishList = (List<WishListEntity>) data;
            model.saveWishList(wishList);
            if (wishList.size() == 0) {
                view.showWishListEmptyView();
            } else {
                view.showWishListView(model.getWishListProducts(), model.getSpecs());
            }
        } else if (data instanceof String) {
            onResume();
        }
    }

    public void onWishItemClick(int position) {
        view.startProductActivity(model.getProductId(position));
    }

    public void onConfirmDeleteWishItemClick(int position) {
        view.showProgressDialog();
        model.deleteWishItem(position, this);
    }
}
