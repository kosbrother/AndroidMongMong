package com.kosbrother.mongmongwoo.utils;

import com.kosbrother.mongmongwoo.model.Product;

import java.util.List;

public class CalculateUtil {

    public static int calculateTotalGoodsPrice(List<Product> shoppingCarProducts) {
        int totalGoodsPrice = 0;
        for (int i = 0; i < shoppingCarProducts.size(); i++) {
            totalGoodsPrice = totalGoodsPrice +
                    shoppingCarProducts.get(i).getFinalPrice() * shoppingCarProducts.get(i).getBuy_count();
        }
        return totalGoodsPrice;
    }
}
