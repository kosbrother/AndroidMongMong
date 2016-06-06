package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.model.Product;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ProductApi {

    public static final String TAG = "PRODUCT_API";
    public static final boolean DEBUG = true;

    public static Product getProductById(int categoryId, int productId) {
        String message = getMessageFromServer("GET", null, UrlCenter.getProductById(categoryId, productId));
        if (message == null) {
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(message, Product.class);
        }
    }

    public static List<Product> getCategoryProducts(int categoryId, int page) {
        String message = getMessageFromServer(
                "GET", null, UrlCenter.getCategoryProducts(categoryId, page));
        if (message == null) {
            return null;
        } else {
            Type listType = new TypeToken<List<Product>>() {
            }.getType();
            return new Gson().fromJson(message, listType);
        }
    }

    public static String getMessageFromServer(String requestMethod, JSONObject json, String apiUrl) {
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
