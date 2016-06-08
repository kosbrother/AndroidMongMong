package com.kosbrother.mongmongwoo.api;

import java.io.IOException;

public class ProductApi {

    public static String getProduct(int categoryId, int productId) throws IOException {
        return RequestUtil.get(UrlCenter.getProductById(categoryId, productId));
    }

    public static String getCategoryProducts(int categoryId, int page) throws IOException {
        return RequestUtil.get(UrlCenter.getCategoryProducts(categoryId, page));
    }

}
