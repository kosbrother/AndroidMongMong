package com.kosbrother.mongmongwoo.api;

import com.kosbrother.mongmongwoo.BuildConfig;

public class UrlCenter {

    public static final String HOST_TEST = "http://104.199.129.36";
    public static final String HOST_PRD = "https://oldsite.mmwooo.com";
    public static final String HOST = BuildConfig.DEBUG ? HOST_TEST : HOST_PRD;
    public static final String API = "/api";
    public static final String API_V3 = "/api/v3";

    public static final String GOOGLE_PLAY_UPDATE =
            "https://play.google.com/store/apps/details?id=com.kosbrother.mongmongwoo&hl=zh_TW";
    public static final String GOOGLE_PLAY_SHARE =
            "https://play.google.com/store/apps/details?id=com.kosbrother.mongmongwoo&referrer=utm_source%3D%2508in_app_navigation";

    public static final String CUSTOMER_SERVICE_LINE = "http://line.me/ti/p/%40kya5456n";
    public static final String CUSTOMER_SERVICE_FB = "https://www.facebook.com/mmwooo";

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

    static String getCategories() {
        return HOST + API_V3 + "/categories";
    }

    static String postRegistrationId() {
        return HOST + API_V3 + "/device_registrations";
    }

    static String getAndroidVersion() {
        return HOST + API + "/android_version";
    }

    static String getShopInfos() {
        return HOST + API_V3 + "/shop_infos";
    }

}
