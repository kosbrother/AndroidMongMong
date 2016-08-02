package com.kosbrother.mongmongwoo.entity.mycollect;

import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;

import java.io.Serializable;

public class WishListEntity implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("item")
    private Product product;
    @SerializedName("item_spec")
    private Spec itemSpec;

    public int getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Spec getItemSpec() {
        return itemSpec;
    }
}
