package com.jasonko.mongmongwoo.api;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kolichung on 3/11/16.
 */
public class MapApi {

    public static final String  TAG   = "MAP_API";
    public static final boolean DEBUG = true;

    public static LatLng getLatLanFromAddress(String address){

        LatLng theLatLng;

        String addressEncode = "";
        try {
             addressEncode = java.net.URLEncoder.encode(address, "UTF-8");
        }catch (Exception e){

        }

        String url = "http://maps.googleapis.com/maps/api/geocode/json?address="
                + addressEncode + "&sensor=true";
        String message = getMessageFromServer("GET", null, null, url);
        if (message == null) {
            return null;
        } else {
            theLatLng = parseLatLng(message);
        }
        return theLatLng;
    }

    private static LatLng parseLatLng(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            JSONObject locationObject;
            if (jsonObject.getJSONArray("results").getJSONObject(0) != null) {
                locationObject  = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            }else {
                locationObject  = jsonObject.getJSONArray("results").getJSONObject(1).getJSONObject("geometry").getJSONObject("location");
            }

            Double latDouble = locationObject.getDouble("lat");
            Double lngDouble = locationObject.getDouble("lng");
            LatLng theLatLng = new LatLng(latDouble,lngDouble);
            Log.i(TAG,theLatLng.toString());
            return theLatLng;
        }catch (Exception e){

        }
        return null;
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
