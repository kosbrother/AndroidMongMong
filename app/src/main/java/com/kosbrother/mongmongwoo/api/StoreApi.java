package com.kosbrother.mongmongwoo.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.model.County;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class StoreApi {

    public static List<County> getCounties() {
        String message = "[{id: 21,name: \"台北市\"},{id: 22,name: \"基隆市\"},{id: 23,name: \"新北市\"},{id: 24,name: \"桃園市\"},{id: 25,name: \"新竹市\"},{id: 26,name: \"新竹縣\"},{id: 27,name: \"苗栗縣\"},{id: 28,name: \"台中市\"},{id: 29,name: \"彰化縣\"},{id: 30,name: \"南投縣\"},{id: 31,name: \"雲林縣\"},{id: 32,name: \"嘉義市\"},{id: 33,name: \"嘉義縣\"},{id: 34,name: \"台南市\"},{id: 35,name: \"高雄市\"},{id: 36,name: \"屏東縣\"},{id: 37,name: \"宜蘭縣\"},{id: 38,name: \"花蓮縣\"},{id: 39,name: \"台東縣\"},{id: 40,name: \"澎湖縣\"},{id: 41,name: \"連江縣\"},{id: 42,name: \"金門縣\"}]";
        Type listType = new TypeToken<List<County>>() {
        }.getType();
        return new Gson().fromJson(message, listType);
    }

    public static String getTowns(int countyId) throws IOException {
        return RequestUtil.get(UrlCenter.getCountryTowns(countyId));
    }

    public static String getRoads(int countyId, int townId) throws IOException {
        return RequestUtil.get(UrlCenter.getCountryTownsRoads(countyId, townId));
    }

    public static String getStores(int county_id, int town_id, int road_id) throws IOException {
        return RequestUtil.get(UrlCenter.getCountryTownRoadStores(county_id, town_id, road_id));
    }

}
