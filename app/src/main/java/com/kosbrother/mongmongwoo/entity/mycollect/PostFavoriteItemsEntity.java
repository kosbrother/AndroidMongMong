package com.kosbrother.mongmongwoo.entity.mycollect;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PostFavoriteItemsEntity implements Serializable{

    @SerializedName("favorite_items")
    private ArrayList<FavoriteItemEntity> favoriteItems;

    public PostFavoriteItemsEntity(ArrayList<FavoriteItemEntity> favoriteItems) {
        this.favoriteItems = favoriteItems;
    }

    public static class FavoriteItemEntity {
        @SerializedName("item_id")
        private int itemId;

        public FavoriteItemEntity(int itemId) {
            this.itemId = itemId;
        }
    }

}
