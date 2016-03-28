package com.jasonko.mongmongwoo.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kolichung on 3/4/16.
 */
public class Product implements Serializable{

    int id;
    String name;
    int price;
    String pic_url;
    int buy_count;

    ArrayList<ProductImage> images;
    ArrayList<ProductSpec> specs;
    String description;

    public void setSelectedSpec(ProductSpec selectedSpec) {
        this.selectedSpec = selectedSpec;
    }

    public ProductSpec getSelectedSpec() {
        return selectedSpec;
    }

    ProductSpec selectedSpec;

    public Product(int id, String name, int price, String pic_url) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.pic_url = pic_url;
        this.buy_count = 0;
    }


    public void setBuy_count(int buy_count) {
        this.buy_count = buy_count;
    }

    public int getBuy_count() {
        return buy_count;
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

    public String getPic_url() {
        return pic_url;
    }

    public void setImages(ArrayList<ProductImage> images) {
        this.images = images;
    }

    public void setSpecs(ArrayList<ProductSpec> specs) {
        this.specs = specs;
    }

    public ArrayList<ProductImage> getImages() {
        if (images == null){
            images = new ArrayList<>();
        }
        return images;
    }

    public ArrayList<ProductSpec> getSpecs() {
        if (specs == null){
            specs = new ArrayList<>();
        }
        return specs;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
