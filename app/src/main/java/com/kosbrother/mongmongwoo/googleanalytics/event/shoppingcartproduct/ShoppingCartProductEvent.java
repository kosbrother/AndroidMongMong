package com.kosbrother.mongmongwoo.googleanalytics.event.shoppingcartproduct;

import com.kosbrother.mongmongwoo.googleanalytics.category.GACategory;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;
import com.kosbrother.mongmongwoo.model.Product;

public class ShoppingCartProductEvent implements GAEvent {

    private Product product;

    public ShoppingCartProductEvent(Product product) {
        this.product = product;
    }

    @Override
    public String getCategory() {
        return GACategory.SHOPPING_CART_PRODUCT;
    }

    @Override
    public String getAction() {
        return product.getName();
    }

    @Override
    public String getLabel() {
        return product.getSelectedSpec().getStyle();
    }

    @Override
    public long getValue() {
        return product.getBuy_count();
    }
}
