package com.kosbrother.mongmongwoo.entity.mycollect;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PostWishListsEntity implements Serializable{

    @SerializedName("wish_lists")
    private ArrayList<WishItemEntity> wishLists;

    public PostWishListsEntity(ArrayList<WishItemEntity> wishLists) {
        this.wishLists = wishLists;
    }

    public static class WishItemEntity {
        @SerializedName("item_id")
        private int itemId;
        @SerializedName("item_spec_id")
        private int itemSpecId;

        public WishItemEntity(int itemId, int itemSpecId) {
            this.itemId = itemId;
            this.itemSpecId = itemSpecId;
        }
    }

}
