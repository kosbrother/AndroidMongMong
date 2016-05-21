package com.kosbrother.mongmongwoo.api;

import com.kosbrother.mongmongwoo.BuildConfig;

public class UrlCenter {

    public static final String HOST_TEST = "http://104.199.129.36";
    public static final String HOST_PRD = "http://api.mmwooo.com";
    public static final String HOST = BuildConfig.DEBUG ? HOST_TEST : HOST_PRD;
    public static final String API = "/api";
    public static final String API_V1 = "/api/v1";
    public static final String API_V2 = "/api/v2";

    public static final String GOOGLE_PLAY_UPDATE =
            "https://play.google.com/store/apps/details?id=com.kosbrother.mongmongwoo&hl=zh_TW";
    public static final String GOOGLE_PLAY_SHARE =
            "https://play.google.com/store/apps/details?id=com.kosbrother.mongmongwoo&referrer=utm_source%3D%2508in_app_navigation";

    public static final String CUSTOMER_SERVICE_LINE = "http://line.me/ti/p/%40kya5456n";
    public static final String CUSTOMER_SERVICE_FB = "https://www.facebook.com/kingofgametw/";

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

    static String getProductById(int productId) {
        return HOST + API_V1
                + "/items/" + productId;
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

    static String getAndroidVersion() {
        return HOST + API + "/android_version";
    }

    static String getOrdersByEmailAndPhone(String email, String phone) {
        return HOST + API_V1
                + "/orders/by_email_phone/"
                + "?email=" + email
                + "&phone=" + phone;
    }
}
