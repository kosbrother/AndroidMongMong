package com.kosbrother.mongmongwoo.entity.myshoppingpoints;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShoppingPointsEntity implements Serializable {

    @SerializedName("point_type")
    private String pointType;
    @SerializedName("amount")
    private int amount;
    @SerializedName("valid_until")
    private String validUntil;
    @SerializedName("is_valid")
    private Boolean isValid;
    @SerializedName("description")
    private String description;
    @SerializedName("shopping_point_records")
    private List<ShoppingPointRecordsEntity> shoppingPointRecords;

    public String getPointType() {
        return pointType;
    }

    public int getAmount() {
        return amount;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public Boolean getValid() {
        return isValid;
    }

    public String getDescription() {
        return description;
    }

    public List<ShoppingPointRecordsEntity> getShoppingPointRecords() {
        return shoppingPointRecords;
    }
}
