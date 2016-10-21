package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.mycollect.FavoriteItemEntity;

import java.util.List;

class FavoritePresenter implements DataManager.ApiCallBack {
    private FavoriteView view;
    private FavoriteManager favoriteManager;

    FavoritePresenter(FavoriteView view, FavoriteManager favoriteManager) {
        this.view = view;
        this.favoriteManager = favoriteManager;
    }

    public void onResume() {
        view.showLoadingView();
        favoriteManager.getMyCollectList(this);
    }

    public void onDestroy() {
        view.hideProgressDialog();
        favoriteManager.cancelRequest(this);
    }

    void onCollectItemClick(int id) {
        view.startProductActivity(id);
    }

    void onCancelCollectConfirmClick(int id) {
        view.showProgressDialog();
        favoriteManager.deleteFavoriteItemsFromId(id, this);
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
                view.showMyCollectList(favoriteManager.getFavoriteProductViewModels());
            }
        } else if (data instanceof String) {
            onResume();
        }
    }

}
