package com.kosbrother.mongmongwoo.entity.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CheckoutPostEntity {
    @SerializedName("user_id")
    @Expose
    public int userId;
    @SerializedName("is_spend_shopping_point")
    @Expose
    public boolean isSpendShoppingPoint;
    @SerializedName("products")
    @Expose
    public List<Product> products = new ArrayList<>();

    public CheckoutPostEntity(int userId, boolean isSpendShoppingPoint, List<Product> products) {
        this.userId = userId;
        this.isSpendShoppingPoint = isSpendShoppingPoint;
        this.products = products;
        // reset post products required value for old version product
        for (Product p : products) {
            p.setQuantity(p.getBuy_count());
            p.setItemSpecId(p.getSelectedSpec().getId());
        }
    }
}
