package com.kosbrother.mongmongwoo.api;

import com.kosbrother.mongmongwoo.BuildConfig;

public class UrlCenter {

    public static final String HOST_TEST = "http://104.199.129.36";
    public static final String HOST_PRD = "https://www.mmwooo.com";
    public static final String HOST = BuildConfig.DEBUG ? HOST_TEST : HOST_PRD;
    public static final String API = "/api";
    public static final String API_V3 = "/api/v3";

    public static final String GOOGLE_PLAY_UPDATE =
            "https://play.google.com/store/apps/details?id=com.kosbrother.mongmongwoo&hl=zh_TW";
    public static final String GOOGLE_PLAY_SHARE =
            "https://play.google.com/store/apps/details?id=com.kosbrother.mongmongwoo&referrer=utm_source%3D%2508in_app_navigation";

    public static final String CUSTOMER_SERVICE_LINE = "http://line.me/ti/p/%40kya5456n";
    public static final String CUSTOMER_SERVICE_FB = "https://www.facebook.com/kingofgametw/";

    static String postUser() {
        return HOST + API_V3 + "/oauth_sessions";
    }

    static String getCountryTowns(int countryId) {
        return HOST + API_V3
                + "/counties/" + countryId
                + "/towns";
    }

    static String getCountryTownsRoads(int county_id, int town_id) {
        return HOST + API_V3
                + "/counties/" + county_id
                + "/towns/" + town_id
                + "/roads";
    }

    static String getCountryTownRoadStores(int countyId, int townId, int road_id) {
        return HOST + API_V3
                + "/counties/" + countyId
                + "/towns/" + townId
                + "/roads/" + road_id
                + "/stores";
    }

    static String getProductById(int categoryId, int productId) {
        return HOST + API_V3
                + "/categories/" + categoryId
                + "/items/" + productId;
    }

    static String getProductBySlug(String categoryName, String slug) {
        return HOST + API_V3
                + "/categories/" + categoryName
                + "/items/" + slug;
    }

    static String getCategories() {
        return HOST + API_V3 + "/categories";
    }

    static String getCategorySortItems(int categoryId, String sortName, int page) {
        return HOST + API_V3
                + "/categories/" + categoryId
                + "/items"
                + "?sort=" + sortName
                + "&page=" + page;
    }

    static String postOrder() {
        return HOST + API_V3 + "/orders";
    }

    static String getOrdersByEmail(String email) {
        return HOST + API_V3
                + "/orders/"
                + "by_user_email"
                + "?email=" + email;
    }

    static String postRegistrationId() {
        return HOST + API_V3 + "/device_registrations";
    }

    static String getAndroidVersion() {
        return HOST + API + "/android_version";
    }

    static String getOrdersByEmailAndPhone(String email, String phone) {
        return HOST + API_V3
                + "/orders/by_email_phone/"
                + "?email=" + email
                + "&phone=" + phone;
    }

    static String getShopInfos() {
        return HOST + API_V3 + "/shop_infos";
    }

    static String register() {
        return HOST + API_V3
                + "/mmw_registrations";
    }

    static String login() {
        return HOST + API_V3
                + "/mmw_registrations"
                + "/login";
    }

    static String forget() {
        return HOST + API_V3
                + "/mmw_registrations"
                + "/forget";
    }

    static String searchItems(String query, int page) {
        return HOST + API_V3
                + "/search_items"
                + "?query=" + query
                + "&page=" + page;
    }

    static String getSuggestions() {
        return HOST + API_V3 + "/item_names";
    }

    static String getHotKeywords() {
        return HOST + API_V3 + "/hot_keywords";
    }

}
