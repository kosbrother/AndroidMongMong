package com.kosbrother.mongmongwoo.api;

import com.kosbrother.mongmongwoo.BuildConfig;

public class UrlCenter {

    public static final String HOST_TEST = "http://106.185.25.83";
    public static final String HOST_PRD = "http://api.mmwooo.com";
    public static final String HOST = BuildConfig.DEBUG ? HOST_TEST : HOST_PRD;
    public static final String API_V1 = "/api/v1";
    public static final String API_V2 = "/api/v2";

    static String postUser() {
        return HOST + API_V1 + "/users";
    }

    static String getTowns(int countryId) {
        return HOST + API_V1
                + "/counties/" + countryId
                + "/towns";
    }

    static String getRoads(int county_id, int town_id) {
        return HOST + API_V1
                + "/counties/" + county_id
                + "/towns/" + town_id
                + "/roads";
    }

    static String getStores(int countyId, int townId, int road_id) {
        return HOST + API_V1
                + "/counties/" + countyId
                + "/towns/" + townId
                + "/roads/" + road_id
                + "/stores";
    }

    static String updateProductById(int productId) {
        return HOST + API_V1
                + "/items/" + productId
                + ".json";
    }

    static String getProductSpec(int productId) {
        return HOST + API_V1
                + "/items/" + productId
                + "/spec_info";
    }

    static String getCategoryProducts(int categoryId, int page) {
        return HOST + API_V1
                + "/categories/" + categoryId
                + "?page=" + page;
    }

    static String postOrder() {
        return HOST + API_V1 + "/orders";
    }

    static String getOrdersByUid(String uid, int page) {
        return HOST + API_V2
                + "/orders/user_owned_orders/" + uid
                + "?page=" + page;
    }

    static String getPastOrderByOrderId(int orderId) {
        return HOST + API_V1
                + "/orders/" + orderId;
    }

    static String postRegistrationId() {
        return HOST + API_V1 + "/device_registrations";
    }
}
