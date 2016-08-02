package com.kosbrother.mongmongwoo.entity.mycollect;

import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.model.Product;

import java.io.Serializable;

public class FavoriteItemEntity implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("item")
    private Product product;

    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }
}
