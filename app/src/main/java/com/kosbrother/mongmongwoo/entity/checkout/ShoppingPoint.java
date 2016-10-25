package com.kosbrother.mongmongwoo.entity.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShoppingPoint {
    @SerializedName("spendable_amount")
    @Expose
    public int spendableAmount;
    @SerializedName("used_amount")
    @Expose
    public int usedAmount;
    @SerializedName("reduced_items_price")
    @Expose
    public int reducedItemsPrice;

    public int getSpendableAmount() {
        return spendableAmount;
    }

    public int getUsedAmount() {
        return usedAmount;
    }

    public int getReducedItemsPrice() {
        return reducedItemsPrice;
    }
}
