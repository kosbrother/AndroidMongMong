package com.kosbrother.mongmongwoo.entity.postorder;

import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.model.Spec;

import java.io.Serializable;

public class UnableToBuyEntity implements Serializable{
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("spec")
    private Spec spec;
    @SerializedName("status")
    private String status;
    @SerializedName("quantity_to_buy")
    private int quantityToBuy;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Spec getSpec() {
        return spec;
    }

    public String getStatus() {
        return status;
    }

    public int getQuantityToBuy() {
        return quantityToBuy;
    }
}
