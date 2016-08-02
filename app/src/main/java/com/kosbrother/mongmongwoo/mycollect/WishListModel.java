package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.mycollect.WishListEntity;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;

import java.util.ArrayList;
import java.util.List;

public class WishListModel {

    private DataManager dataManager;
    private List<WishListEntity> wishListItemEntities;

    public WishListModel(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void getWishList(DataManager.ApiCallBack callBack) {
        dataManager.getWishLists(Settings.getSavedUser().getUserId(), callBack);
    }

    public void saveWishList(List<WishListEntity> wishListItemEntities) {
        this.wishListItemEntities = wishListItemEntities;
    }

    public List<Product> getWishListProducts() {
        List<Product> list = new ArrayList<>();
        for (WishListEntity wishListItem : wishListItemEntities) {
            list.add(wishListItem.getProduct());
        }
        return list;
    }

    public List<Spec> getSpecs() {
        List<Spec> list = new ArrayList<>();
        for (WishListEntity wishListItem :
                wishListItemEntities) {
            list.add(wishListItem.getItemSpec());
        }
        return list;
    }

    public int getProductId(int position) {
        return wishListItemEntities.get(position).getProduct().getId();
    }

    public void deleteWishItem(int position, DataManager.ApiCallBack callBack) {
        int userId = Settings.getSavedUser().getUserId();
        int itemSpecId = wishListItemEntities.get(position).getItemSpec().getId();
        dataManager.deleteWishListsItemSpecs(userId, itemSpecId, callBack);
    }
}
