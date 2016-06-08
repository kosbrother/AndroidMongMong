package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

public class Town {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public Town(int id, String name) {
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
