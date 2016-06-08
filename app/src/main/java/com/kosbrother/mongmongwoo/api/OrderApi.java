package com.kosbrother.mongmongwoo.api;

import java.io.IOException;

public class OrderApi {

    public static String getOrdersByUid(String uid, int page) throws IOException {
        return RequestUtil.get(UrlCenter.getOrdersByUid(uid, page));
    }

    public static String getPastOrderByOrderId(int orderId) throws IOException {
        return RequestUtil.get(UrlCenter.getPastOrderByOrderId(orderId));
    }

    public static String getOrdersByEmailAndPhone(String email, String phone) throws IOException {
        return RequestUtil.get(UrlCenter.getOrdersByEmailAndPhone(email, phone));
    }

    public static String postOrder(String json) throws IOException {
        return RequestUtil.post(UrlCenter.postOrder(), json);
    }

}
