package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

public class County {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public County(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
