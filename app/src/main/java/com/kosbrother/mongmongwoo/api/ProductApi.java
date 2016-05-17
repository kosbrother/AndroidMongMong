package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import com.google.gson.Gson;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;

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

public class ProductApi {

    public static final String TAG = "PRODUCT_API";
    public static final boolean DEBUG = true;

    public static Product getProductById(int productId) {
        String message = getMessageFromServer("GET", null, null, UrlCenter.getProductById(productId));
        if (message == null) {
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(message, Product.class);
        }
    }

    public static ArrayList<Spec> getProductSpects(int productId) {
        ArrayList<Spec> specses = new ArrayList<>();
        String message = getMessageFromServer("GET", null, null, UrlCenter.getProductSpec(productId));
        if (message == null) {
            return null;
        } else {
            return parseProductSytles(message, specses);
        }
    }

    public static ArrayList<Product> getCategoryProducts(int categoryId, int page) {
        ArrayList<Product> products = new ArrayList<>();
        String message = getMessageFromServer(
                "GET", null, null, UrlCenter.getCategoryProducts(categoryId, page));
        if (message == null) {
            return null;
        } else {
            parseItems(products, message);
        }
        return products;
    }

    private static ArrayList<Spec> parseProductSytles(String message, ArrayList<Spec> specs) {

        try {
            JSONArray specArray = new JSONArray(message);
            for (int i = 0; i < specArray.length(); i++) {
                // TODO: 2016/5/17 spec json key is different from product api can not use gson parse
                JSONObject specObject = specArray.getJSONObject(i);
                int id = specObject.getInt("id");
                String style = specObject.getString("style");
                int amount = 0;
                try {
                    amount = specObject.getInt("style_amount");
                } catch (Exception e) {

                }
                String pic_url = specObject.getString("style_pic");
                specs.add(new Spec(id, style, amount, pic_url));
            }

            return specs;

        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }

        return null;
    }

    private static void parseItems(ArrayList<Product> products, String message) {
        try {
            JSONArray itemsArray = new JSONArray(message);
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);

                int id = 0;
                String name = "";
                int price = 0;
                String cover = "";

                try {
                    id = itemObject.getInt("id");
                    name = itemObject.getString("name");
                    price = itemObject.getInt("price");
                    cover = itemObject.getJSONObject("cover").getString("url");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Product newProduct = new Product(id, name, price, cover);
                products.add(newProduct);
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
