package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import com.kosbrother.mongmongwoo.model.County;
import com.kosbrother.mongmongwoo.model.Road;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.Town;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kolichung on 3/14/16.
 */
public class StoreApi {

    public static final String TAG = "MONMON_API";
    public static final boolean DEBUG = true;

    public static ArrayList<County> getCounties() {
        ArrayList<County> counties = new ArrayList<>();
        String message = "[{\"id\":1,\"name\":\"台北市\"},{\"id\":2,\"name\":\"基隆市\"},{\"id\":3,\"name\":\"宜蘭縣\"},{\"id\":4,\"name\":\"新北市\"},{\"id\":5,\"name\":\"新竹市\"},{\"id\":6,\"name\":\"新竹縣\"},{\"id\":7,\"name\":\"桃園市\"},{\"id\":8,\"name\":\"苗栗縣\"},{\"id\":9,\"name\":\"台中市\"},{\"id\":10,\"name\":\"南投縣\"},{\"id\":11,\"name\":\"彰化縣\"},{\"id\":12,\"name\":\"嘉義市\"},{\"id\":13,\"name\":\"嘉義縣\"},{\"id\":14,\"name\":\"雲林縣\"},{\"id\":15,\"name\":\"台南市\"},{\"id\":16,\"name\":\"澎湖縣\"},{\"id\":17,\"name\":\"高雄市\"},{\"id\":18,\"name\":\"台東縣\"},{\"id\":19,\"name\":\"屏東縣\"},{\"id\":20,\"name\":\"花蓮縣\"}]";
        parseCounties(counties, message);
        return counties;
    }

    private static void parseCounties(ArrayList<County> counties, String message) {

        try {
            JSONArray itemsArray = new JSONArray(message);
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);

                int county_id = 0;
                String name = "";

                try {
                    county_id = itemObject.getInt("id");
                    name = itemObject.getString("name");
                } catch (Exception e) {

                }
                County newCounty = new County(county_id, name);
                counties.add(newCounty);
            }
        } catch (Exception e) {

        }

    }

    public static ArrayList<Town> getTowns(int countyId) {
        ArrayList<Town> towns = new ArrayList<>();
        String message = getMessageFromServer("GET", null, null, UrlCenter.getTowns(countyId));
        if (message == null) {
            return null;
        } else {
            parseTowns(towns, message);
        }
        return towns;
    }

    private static void parseTowns(ArrayList<Town> towns, String message) {

        try {
            JSONArray itemsArray = new JSONArray(message);
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);

                int town_id = 0;
                String name = "";

                try {
                    town_id = itemObject.getInt("id");
                    name = itemObject.getString("name");
                } catch (Exception e) {

                }
                Town newTown = new Town(town_id, name);
                towns.add(newTown);
            }
        } catch (Exception e) {

        }
    }

    public static ArrayList<Road> getRoads(int countyId, int townId) {
        ArrayList<Road> roads = new ArrayList<>();
        String message = getMessageFromServer("GET", null, null, UrlCenter.getRoads(countyId, townId));
        if (message == null) {
            return null;
        } else {
            parseRoads(roads, message);
        }
        return roads;
    }

    private static void parseRoads(ArrayList<Road> roads, String message) {

        try {
            JSONArray itemsArray = new JSONArray(message);
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);

                int road_id = 0;
                String name = "";

                try {
                    road_id = itemObject.getInt("id");
                    name = itemObject.getString("name");
                } catch (Exception e) {

                }
                Road newRoad = new Road(road_id, name);
                roads.add(newRoad);
            }
        } catch (Exception e) {

        }

    }

    public static ArrayList<Store> getStores(int county_id, int town_id, int road_id) {
        ArrayList<Store> stores = new ArrayList<>();
        String message = getMessageFromServer("GET", null, null, UrlCenter.getStores(county_id, town_id, road_id));
        if (message == null) {
            return null;
        } else {
            parseStore(stores, message);
        }
        return stores;
    }

    private static void parseStore(ArrayList<Store> stores, String message) {

        try {
            JSONArray itemsArray = new JSONArray(message);
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);

                int store_id = 0;
                String store_code = "";
                String name = "";
                String address = "";
                double lat = 0;
                double lng = 0;

                try {
                    store_id = itemObject.getInt("id");
                    store_code = itemObject.getString("store_code");
                    name = itemObject.getString("name");
                    address = itemObject.getString("address");
                    lat = itemObject.getDouble("lat");
                    lng = itemObject.getDouble("lng");
                } catch (Exception e) {

                }
                Store newStore = new Store(store_id, store_code, name, address);
                newStore.setLatLng(lat, lng);
                stores.add(newStore);
            }
        } catch (Exception e) {

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
