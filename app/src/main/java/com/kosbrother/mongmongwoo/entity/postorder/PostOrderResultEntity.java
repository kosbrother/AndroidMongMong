package com.kosbrother.mongmongwoo.entity.postorder;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostOrderResultEntity implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("unable_to_buy")
    private List<UnableToBuyModel> unableToBuyModels = new ArrayList<>();

    public int getId() {
        return id;
    }

    public List<UnableToBuyModel> getUnableToBuyModels() {
        return unableToBuyModels;
    }

}
