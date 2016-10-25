package com.kosbrother.mongmongwoo.entity.checkout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kosbrother.mongmongwoo.common.ProductViewModel;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CheckoutResultEntity {
    @SerializedName("products")
    @Expose
    public List<Product> products = new ArrayList<>();
    @SerializedName("order_price")
    @Expose
    public OrderPrice orderPrice;

    public List<Product> getProducts() {
        return products;
    }

    public OrderPrice getOrderPrice() {
        return orderPrice;
    }

    public List<ProductViewModel> getProductViewModels() {
        List<ProductViewModel> list = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            list.add(new ProductViewModel(product, i));
        }
        return list;
    }
}
