package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.mycollect.FavoriteItemEntity;

import java.util.List;

public class FavoritePresenter implements DataManager.ApiCallBack {
    private FavoriteView view;
    private FavoriteModel model;

    public FavoritePresenter(FavoriteView view, FavoriteModel model) {
        this.view = view;
        this.model = model;
    }

    public void onResume() {
        view.showLoadingView();
        model.getMyCollectList(this);
    }

    public void onCollectItemClick(int position) {
        view.startProductActivity(model.getProductId(position));
    }

    public void onCancelCollectConfirmClick(int position) {
        view.showProgressDialog();
        model.cancelCollect(position, this);
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
            List<FavoriteItemEntity> myCollectEntities = (List<FavoriteItemEntity>) data;
            if (myCollectEntities.size() == 0) {
                view.showEmptyView();
            } else {
                view.showMyCollectList(model.getProductList());
            }
        } else if (data instanceof String) {
            onResume();
        }
    }

}
