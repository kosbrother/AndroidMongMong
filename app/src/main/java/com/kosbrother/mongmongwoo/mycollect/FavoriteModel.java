package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.List;

public class FavoriteModel {
    private FavoriteManager notificationManager;

    public FavoriteModel(FavoriteManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public void getMyCollectList(DataManager.ApiCallBack callBack) {
        notificationManager.getMyCollectList(callBack);
    }

    public List<Product> getProductList() {
        return notificationManager.getProductList();
    }

    public int getProductId(int position) {
        return getProductList().get(position).getId();
    }

    public void cancelCollect(int position, DataManager.ApiCallBack callBack) {
        int itemId = getProductList().get(position).getId();
        notificationManager.deleteFavoriteItems(itemId, callBack);
    }
}
