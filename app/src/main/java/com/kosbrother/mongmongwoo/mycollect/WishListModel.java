package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.mycollect.WishListEntity;

import java.util.ArrayList;
import java.util.List;

class WishListModel {

    private DataManager dataManager;
    private List<WishListEntity> wishListItemEntities;

    WishListModel(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    void getWishList(DataManager.ApiCallBack callBack) {
        dataManager.getWishLists(Settings.getSavedUser().getUserId(), callBack);
    }

    void saveWishList(List<WishListEntity> wishListItemEntities) {
        this.wishListItemEntities = wishListItemEntities;
    }

    List<WishListViewModel> getWishListViewModels() {
        List<WishListViewModel> list = new ArrayList<>();
        for (WishListEntity wishListItem : wishListItemEntities) {
            list.add(new WishListViewModel(wishListItem.getProduct(), wishListItem.getItemSpec()));
        }
        return list;
    }

    void deleteWishItem(int specId, DataManager.ApiCallBack callBack) {
        int userId = Settings.getSavedUser().getUserId();
        dataManager.deleteWishListsItemSpecs(userId, specId, callBack);
    }

    void cancelRequest(DataManager.ApiCallBack callBack) {
        dataManager.unSubscribe(callBack);
    }
}
