package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.ProductImage;
import com.kosbrother.mongmongwoo.model.ProductSpec;

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
 * Created by kolichung on 3/4/16.
 */
public class ProductApi {

    public static final String  TAG   = "PRODUCT_API";
    public static final boolean DEBUG = true;
    public static final String host = "http://api.kosbrother.com";

    public static Product updateProductById(int product_id, Product theProduct){
        String url = host + "/api/v1/items/"+Integer.toString(product_id)+".json";
        String message = getMessageFromServer("GET", null, null, url);
        if (message == null) {
            return null;
        } else {
            return parseItem(message, theProduct);
        }
    }

    public static ArrayList<ProductSpec> getProductSpects(int product_id){
        ArrayList<ProductSpec> productSpecs = new ArrayList<>();
        String url = host + "/api/v1/items/"+Integer.toString(product_id)+"/spec_info";
        String message = getMessageFromServer("GET", null, null, url);
        if (message == null) {
            return null;
        } else {
            return parseProductSytles(message, productSpecs);
        }
    }

    private static ArrayList<ProductSpec> parseProductSytles(String message, ArrayList<ProductSpec> specs) {

        try{
            JSONArray specArray = new JSONArray(message);
            for (int i=0; i< specArray.length(); i++){
                JSONObject specObject = specArray.getJSONObject(i);
                int id = specObject.getInt("id");
                String style = specObject.getString("style");
                int amount = 0;
                try {
                    amount = specObject.getInt("style_amount");
                }catch (Exception e){

                }
                String pic_url = host+specObject.getString("style_pic");
                specs.add(new ProductSpec(id,style,amount,pic_url));
            }

            return  specs;

        }catch (Exception e){
            Log.i(TAG,e.toString());
        }

        return null;
    }


    private static Product parseItem(String message, Product theProduct) {
        try {

            JSONObject jsonObject = new JSONObject(message);

            JSONArray photosArray = jsonObject.getJSONArray("photos");
            JSONArray specArray = jsonObject.getJSONArray("specs");

            String description = "";
            ArrayList<ProductImage> images = new ArrayList<>();
            ArrayList<ProductSpec> specs = new ArrayList<>();

            try {
                description = jsonObject.getString("description");
            }catch (Exception e){

            }

            for (int i=0; i< photosArray.length(); i++ ){
                JSONObject photoObject = photosArray.getJSONObject(i);
                String imageUrl = host + photoObject.getString("image_url");
                String intro = photoObject.getString("photo_intro");
                images.add(new ProductImage(imageUrl,"","", intro));
            }

            for (int i=0; i< specArray.length(); i++){
                JSONObject specObject = specArray.getJSONObject(i);
                int id = specObject.getInt("id");
                String style = specObject.getString("style");
                int amount = 0;
                try {
                    amount = specObject.getInt("amount");
                }catch (Exception e){

                }
                String pic_url = host+specObject.getString("pic");
                specs.add(new ProductSpec(id,style,amount,pic_url));
            }


            theProduct.setDescription(description);
            theProduct.setImages(images);
            theProduct.setSpecs(specs);

            return  theProduct;

        }catch (Exception e){
            Log.i(TAG,e.toString());
        }
        return null;
    }


    public static ArrayList<Product> getCategoryProducts(int category_id, int page){
        ArrayList<Product> products = new ArrayList<>();
        String url = host + "/api/v1/categories/"+ Integer.toString(category_id)+"?page="+Integer.toString(page);
        String message = getMessageFromServer("GET", null, null, url);
        if (message == null) {
            return null;
        } else {
            parseItems(products, message);
        }
        return products;
    }

    private static void parseItems(ArrayList<Product> products, String message) {
        try {
            JSONArray itemsArray = new JSONArray(message);
            for (int i = 0; i < itemsArray.length(); i++){
                JSONObject itemObject = itemsArray.getJSONObject(i);

                int id = 0;
                String name = "";
                int price = 0;
                String pic_url = "";

                try {
                    id = itemObject.getInt("id");
                    name = itemObject.getString("name");
                    price = itemObject.getInt("price");
                    pic_url = host + itemObject.getJSONObject("cover").getString("url");
                }catch (Exception e){

                }
                Product newProduct = new Product(id,name,price,pic_url);
                products.add(newProduct);
            }
        }catch (Exception e){

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
