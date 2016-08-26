package com.kosbrother.mongmongwoo.entity.myshoppingpoints;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShoppingPointsDetailEntity implements Serializable {

    @SerializedName("total")
    private int total;
    @SerializedName("shopping_points")
    private List<ShoppingPointsEntity> shoppingPoints;

    public int getTotal() {
        return total;
    }

    public List<ShoppingPointsEntity> getShoppingPoints() {
        return shoppingPoints;
    }
}
