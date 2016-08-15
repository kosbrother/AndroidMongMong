package com.kosbrother.mongmongwoo.model;

import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.api.UrlCenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    private static final String ITEM_URI_STRING =
            UrlCenter.HOST + "/categories/%s/items/%s";

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private int price;
    @SerializedName("slug")
    private String slug;
    @SerializedName("status")
    private String status;
    @SerializedName("description")
    private String description;
    @SerializedName("cover")
    private Cover cover;
    @SerializedName("special_price")
    private int specialPrice;
    @SerializedName("specs")
    private ArrayList<Spec> specs;
    @SerializedName("photos")
    private ArrayList<Photo> photos;
    @SerializedName("final_price")
    private int finalPrice;

    private int buy_count;
    private Spec selectedSpec;
    private String categoryName;
    private int categoryId;

    public Product(int id, String name, int price, String coverUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.buy_count = 0;
        this.cover = new Cover(coverUrl);
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

    /**
     * Product slug for google app indexing.
     *
     * @return product slug.
     */
    public String getSlug() {
        return slug;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Cover getCover() {
        return cover;
    }

    public List<Spec> getSpecs() {
        return specs;
    }

    public List<Photo> getPhotos() {
        return photos;
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isShareUrlValid() {
        return categoryName != null && !categoryName.isEmpty();
    }

    public String getUrl() {
        return String.format(ITEM_URI_STRING, categoryName, name);
    }

    public int getFinalPrice() {
        if (finalPrice != 0) {
            return finalPrice;
        }
        if (specialPrice != 0) {
            return specialPrice;
        }
        return price;
    }

    public String getFinalPriceText() {
        return "NT$" + getFinalPrice();
    }

    public String getOriginalPriceText() {
        String specialPriceText;
        if (isSpecial()) {
            specialPriceText = "原價NT$ " + price;
        } else {
            specialPriceText = "";
        }
        return specialPriceText;
    }

    public boolean isSpecial() {
        return price != getFinalPrice();
    }

}
