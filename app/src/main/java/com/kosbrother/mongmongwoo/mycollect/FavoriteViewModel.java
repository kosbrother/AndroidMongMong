package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.model.Cover;
import com.kosbrother.mongmongwoo.model.Product;

public class FavoriteViewModel {

    private static final String FINAL_PRICE_TITLE = "NT$";
    private static final String ORIGINAL_PRICE_TITLE = "原價NT$ ";

    private final int id;
    private final String name;
    private final String finalPriceText;
    private final String originalPriceText;
    private final boolean isSpecialPrice;
    private final String coverUrlString;
    private final String discountIconUrlString;

    FavoriteViewModel(Product product) {
        id = product.getId();
        name = product.getName();
        finalPriceText = FINAL_PRICE_TITLE + product.getFinalPrice();
        originalPriceText = ORIGINAL_PRICE_TITLE + product.getPrice();
        isSpecialPrice = product.isSpecial();
        Cover cover = product.getCover();
        coverUrlString = cover != null ? cover.getUrl() : "";
        discountIconUrlString = product.getDiscountIconUrl();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFinalPriceText() {
        return finalPriceText;
    }

    public String getOriginalPriceText() {
        return originalPriceText;
    }

    public boolean isSpecialPrice() {
        return isSpecialPrice;
    }

    public String getCoverUrlString() {
        return coverUrlString;
    }

    public String getDiscountIconUrlString() {
        return discountIconUrlString;
    }
}
