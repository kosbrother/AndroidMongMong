package com.kosbrother.mongmongwoo.mycollect;

import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;

public class WishListViewModel {

    private static final String FINAL_PRICE_TITLE = "NT$";
    private static final String ORIGINAL_PRICE_TITLE = "原價NT$ ";
    private static final String CURRENT_AMOUNT_TITLE = "現貨數：";
    private static final String ENOUGH_AMOUNT = "庫存充分";

    private final int productId;
    private final String name;
    private final String finalPriceText;
    private final String originalPriceText;
    private final boolean isSpecialPrice;
    private final String discountIconUrlString;

    private final int specId;
    private final String styleUrlString;
    private final String specName;
    private final String currentStockAmountText;
    private final boolean showGoShoppingText;

    WishListViewModel(Product product, Spec spec) {
        productId = product.getId();
        name = product.getName();
        finalPriceText = FINAL_PRICE_TITLE + product.getFinalPrice();
        originalPriceText = ORIGINAL_PRICE_TITLE + product.getPrice();
        isSpecialPrice = product.isSpecial();
        discountIconUrlString = product.getDiscountIconUrl();

        specId = spec.getId();
        specName = spec.getStyle();
        Spec.StylePic stylePic = spec.getStylePic();
        styleUrlString = stylePic != null ? stylePic.getUrl() : "";

        int stockAmount = spec.getStockAmount();
        if (stockAmount >= 10) {
            currentStockAmountText = CURRENT_AMOUNT_TITLE + ENOUGH_AMOUNT;
        } else {
            currentStockAmountText = CURRENT_AMOUNT_TITLE + stockAmount;
        }

        showGoShoppingText = stockAmount > 0;
    }

    public int getProductId() {
        return productId;
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

    public String getStyleUrlString() {
        return styleUrlString;
    }

    public String getDiscountIconUrlString() {
        return discountIconUrlString;
    }

    public int getSpecId() {
        return specId;
    }

    public String getSpecName() {
        return specName;
    }

    public String getCurrentStockAmountText() {
        return currentStockAmountText;
    }

    public boolean isShowGoShoppingText() {
        return showGoShoppingText;
    }
}
