package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.model.County;
import com.kosbrother.mongmongwoo.model.Road;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.Town;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StoreApi {

    public static final String TAG = "MONMON_API";
    public static final boolean DEBUG = true;

    public static List<County> getCounties() {
        String message = "[{id: 21,name: \"台北市\"},{id: 22,name: \"基隆市\"},{id: 23,name: \"新北市\"},{id: 24,name: \"桃園市\"},{id: 25,name: \"新竹市\"},{id: 26,name: \"新竹縣\"},{id: 27,name: \"苗栗縣\"},{id: 28,name: \"台中市\"},{id: 29,name: \"彰化縣\"},{id: 30,name: \"南投縣\"},{id: 31,name: \"雲林縣\"},{id: 32,name: \"嘉義市\"},{id: 33,name: \"嘉義縣\"},{id: 34,name: \"台南市\"},{id: 35,name: \"高雄市\"},{id: 36,name: \"屏東縣\"},{id: 37,name: \"宜蘭縣\"},{id: 38,name: \"花蓮縣\"},{id: 39,name: \"台東縣\"},{id: 40,name: \"澎湖縣\"},{id: 41,name: \"連江縣\"},{id: 42,name: \"金門縣\"}]";
        Type listType = new TypeToken<List<County>>() {
        }.getType();
        return new Gson().fromJson(message, listType);
    }

    public static List<Town> getTowns(int countyId) {
        String message = getMessageFromServer("GET", null, null, UrlCenter.getCountryTowns(countyId));
        if (message == null) {
            return null;
        } else {
            Type listType = new TypeToken<List<Town>>() {
            }.getType();
            return new Gson().fromJson(message, listType);
        }
    }

    public static List<Road> getRoads(int countyId, int townId) {
        String message = getMessageFromServer("GET", null, null, UrlCenter.getCountryTownsRoads(countyId, townId));
        if (message == null) {
            return null;
        } else {
            Type listType = new TypeToken<List<Road>>() {
            }.getType();
            return new Gson().fromJson(message, listType);
        }
    }

    public static ArrayList<Store> getStores(int county_id, int town_id, int road_id) {
        String message = getMessageFromServer("GET", null, null, UrlCenter.getCountryTownRoadStores(county_id, town_id, road_id));
        if (message == null) {
            return null;
        } else {
            Type listType = new TypeToken<List<Store>>() {
            }.getType();
            return new Gson().fromJson(message, listType);
        }
    }

    public static String getMessageFromServer(String requestMethod, String apiPath, JSONObject json, String apiUrl) {
        URL url;
        try {

            url = new URL(apiUrl);

            if (DEBUG)
                Log.d(TAG, "URL: " + url);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);

            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            if (requestMethod.equalsIgnoreCase("POST"))
                connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.connect();

            if (requestMethod.equalsIgnoreCase("POST")) {
                OutputStream outputStream;

                outputStream = connection.getOutputStream();
                if (DEBUG)
                    Log.d("post message", json.toString());

                outputStream.write(json.toString().getBytes());
                outputStream.flush();
                outputStream.close();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder lines = new StringBuilder();

            String tempStr;

            while ((tempStr = reader.readLine()) != null) {
                lines = lines.append(tempStr);
            }
            if (DEBUG)
                Log.d("MOVIE_API", lines.toString());

            reader.close();
            connection.disconnect();

            return lines.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
