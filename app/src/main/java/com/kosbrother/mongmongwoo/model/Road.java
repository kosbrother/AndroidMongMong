package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

public class Road {

    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;

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
