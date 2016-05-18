package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

public class Town {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
