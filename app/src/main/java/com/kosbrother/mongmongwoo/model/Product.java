package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private int price;
    @SerializedName("cover")
    private String cover;
    @SerializedName("description")
    private String description;
    @SerializedName("status")
    private String status;

    @SerializedName("photos")
    private List<Photo> images;
    @SerializedName("specs")
    private List<Spec> specs;

    private int buy_count;
    private Spec selectedSpec;
    private String categoryName;

    public Product(int id, String name, int price, String cover) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.cover = cover;
        this.buy_count = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCover() {
        return cover;
    }

    public List<Photo> getImages() {
        return images;
    }

    public List<Spec> getSpecs() {
        return specs;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setSelectedSpec(Spec selectedSpec) {
        this.selectedSpec = selectedSpec;
    }

    public Spec getSelectedSpec() {
        return selectedSpec;
    }

    public void setBuy_count(int buy_count) {
        this.buy_count = buy_count;
    }

    public int getBuy_count() {
        return buy_count;
    }

    public boolean isOnShelf() {
        return status.contains("on_shelf");
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
