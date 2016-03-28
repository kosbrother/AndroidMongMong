package com.jasonko.mongmongwoo.api;

import android.util.Log;

import com.jasonko.mongmongwoo.model.PastOrder;
import com.jasonko.mongmongwoo.model.PastOrderProduct;
import com.jasonko.mongmongwoo.model.Product;
import com.jasonko.mongmongwoo.model.Store;

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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kolichung on 3/15/16.
 */
public class OrderApi {

    public static final String  TAG   = "ORDER_API";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String host = "http://106.185.34.142";
    public static final boolean DEBUG = true;

    public static ArrayList<PastOrder> getOrdersByUid(String uid){
        ArrayList<PastOrder> pastOrders = new ArrayList<>();
        String url = host + "/api/v1/orders/user_owned_orders/"+ uid;
        String message = getMessageFromServer("GET", null, null, url);
        if (message == null) {
            return null;
        } else {
            return parsePastOrders(message, pastOrders);
        }
    }

    public static PastOrder getPastOrderByOrderId(int order_id, PastOrder theOrder){
        String url = host + "/api/v1/orders/"+ Integer.toString(order_id);
        String message = getMessageFromServer("GET", null, null, url);
        if (message == null) {
            return null;
        } else {
            return parseTheOrder(message, theOrder);
        }
    }

    private static PastOrder parseTheOrder(String message, PastOrder theOder) {

        try {
            JSONObject jsonObject = new JSONObject(message);

            String shippingName = "";
            String shippingPhone = "";
            Store shippingStore;
            ArrayList<PastOrderProduct> orderProducts = new ArrayList<>();
            int shipPrice = 0;
            int productPrice = 0;
            int totalPrice = 0;

            String storeCode= "";
            int store_id = 0;

            try{
                productPrice = jsonObject.getInt("items_price");
                shipPrice =  jsonObject.getInt("ship_fee");
                totalPrice = jsonObject.getInt("total");
                JSONObject infoObject = jsonObject.getJSONObject("info");
                shippingName = infoObject.getString("ship_name");
                shippingPhone = infoObject.getString("ship_phone");

                storeCode = infoObject.getString("ship_store_code");
                store_id = infoObject.getInt("ship_store_id");

            }catch (Exception e){

            }
            shippingStore = new Store(store_id,storeCode,"","");

            try{
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < itemsArray.length(); i++){
                    JSONObject itemObject = itemsArray.getJSONObject(i);

                    String name = "";
                    String style = "";
                    int quantity = 0;
                    int price = 0;

                    try {
                        name = itemObject.getString("name");
                        style = itemObject.getString("style");
                        quantity = itemObject.getInt("quantity");
                        price = itemObject.getInt("price");
                    }catch (Exception e){

                    }
                    PastOrderProduct pastOrderProduct = new PastOrderProduct(name,style,quantity,price);
                    orderProducts.add(pastOrderProduct);
                }
            }catch (Exception e){

            }

            theOder.setShippingName(shippingName);
            theOder.setPastOrderProducts(orderProducts);
            theOder.setProductPrice(productPrice);
            theOder.setShippingPhone(shippingPhone);
            theOder.setShippingStore(shippingStore);
            theOder.setShipPrice(shipPrice);

            return theOder;
        }catch (Exception e){

        }
        return null;

    }

    private static ArrayList<PastOrder> parsePastOrders(String message, ArrayList<PastOrder> pastOrders) {

        try {
            JSONArray itemsArray = new JSONArray(message);
            for (int i = 0; i < itemsArray.length(); i++){
                JSONObject itemObject = itemsArray.getJSONObject(i);

                int order_id =0;
                int total_price = 0;
                String date = "";
                String status = "";

                try {
                    order_id = itemObject.getInt("id");
                    total_price = itemObject.getInt("total");
                    date = itemObject.getString("created_at");
                    status = itemObject.getString("status");
                    switch (status){
                        case "order_placed":
                            status = "尚未出貨";
                            break;
                        case "item_shipping":
                            status = "已出貨";
                            break;
                        case  "item_shipped":
                            status = "已取貨";
                            break;
                        case  "order_cancelled":
                            status = "訂單取消";
                            break;
                        default:
                            status = "尚未出貨";
                    }
                }catch (Exception e){

                }
                PastOrder newProduct = new PastOrder(order_id,total_price,date,status);
                pastOrders.add(newProduct);
            }
            return pastOrders;
        }catch (Exception e){

        }
        return null;
    }

    public static String httpPostOrder(String uid, int items_price, int ship_fee, int total, String ship_name, String ship_phone, String ship_store_code, int ship_store_id, ArrayList<Product> products){

        String url = host + "/api/v1/orders";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", uid);
            jsonObject.put("items_price", Integer.toString(items_price));
            jsonObject.put("ship_fee", Integer.toString(ship_fee));
            jsonObject.put("total", Integer.toString(total));
            jsonObject.put("ship_name", ship_name);
            jsonObject.put("ship_phone", ship_phone);
            jsonObject.put("ship_store_code", ship_store_code);
            jsonObject.put("ship_store_id", Integer.toString(ship_store_id));
//            jsonObject.put("products[][name]","產品名");

            JSONArray productsArray = new JSONArray();
            JSONObject productObject = new JSONObject();
            productObject.put("name", "產品222");
            productObject.put("quantity", Integer.toString(3));
            productObject.put("price", Integer.toString(2000));
            productsArray.put(productObject);
            jsonObject.put("products",productsArray);
            return post(url, jsonObject.toString());
        }catch (Exception e){
            Log.i("HTTP error", e.toString());
        }
        return "success upload";

    }

    public static String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        Log.i(TAG, response.body().toString());
        return response.body().string();
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
