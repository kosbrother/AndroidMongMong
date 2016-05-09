package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import com.kosbrother.mongmongwoo.model.PastOrder;
import com.kosbrother.mongmongwoo.model.PastOrderProduct;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Store;

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

    public static final String TAG = "ORDER_API";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final boolean DEBUG = true;

    public static ArrayList<PastOrder> getOrdersByUid(String uid, int page) {
        ArrayList<PastOrder> pastOrders = new ArrayList<>();
        String message = getMessageFromServer("GET", null, null, UrlCenter.getOrdersByUid(uid, page));
        if (message == null) {
            return null;
        } else {
            return parsePastOrders(message, pastOrders);
        }
    }

    public static PastOrder getPastOrderByOrderId(int orderId) {
        String message = getMessageFromServer(
                "GET", null, null, UrlCenter.getPastOrderByOrderId(orderId));
        if (message == null) {
            return null;
        } else {
            PastOrder pastOrder = new PastOrder(orderId, 0, "", "");
            return parseTheOrder(message, pastOrder);
        }
    }

    private static PastOrder parseTheOrder(String message, PastOrder theOder) {
        try {
            JSONObject jsonObject = new JSONObject(message);

            String status = "";
            String date = "";
            String shipName = "";
            String shipPhone = "";
            Store shippingStore;
            ArrayList<PastOrderProduct> orderProducts = new ArrayList<>();
            int shipFee = 0;
            int itemPrice = 0;
            int total = 0;
            String note = "";

            String storeCode = "";
            int store_id = 0;
            String storeName = "";

            try {
                status = jsonObject.getString("status");
                date = jsonObject.getString("created_on");
                itemPrice = jsonObject.getInt("items_price");
                shipFee = jsonObject.getInt("ship_fee");
                total = jsonObject.getInt("total");
                note = jsonObject.getString("note");

                JSONObject infoObject = jsonObject.getJSONObject("info");
                shipName = infoObject.getString("ship_name");
                shipPhone = infoObject.getString("ship_phone");

                storeCode = infoObject.getString("ship_store_code");
                store_id = infoObject.getInt("ship_store_id");
                storeName = infoObject.getString("ship_store_name");
            } catch (Exception e) {
                e.printStackTrace();
            }
            shippingStore = new Store(store_id, storeCode, storeName, "");

            try {
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < itemsArray.length(); i++) {
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    PastOrderProduct pastOrderProduct = new PastOrderProduct(name, style, quantity, price);
                    orderProducts.add(pastOrderProduct);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            theOder.setStatus(status);
            theOder.setDate(date);
            theOder.setItemPrice(itemPrice);
            theOder.setShipFee(shipFee);
            theOder.setTotalPrice(total);
            theOder.setNote(note);

            theOder.setShipName(shipName);
            theOder.setShipPhone(shipPhone);
            theOder.setShippingStore(shippingStore);

            theOder.setPastOrderProducts(orderProducts);
            return theOder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static ArrayList<PastOrder> parsePastOrders(String message, ArrayList<PastOrder> pastOrders) {

        try {
            JSONArray itemsArray = new JSONArray(message);
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject itemObject = itemsArray.getJSONObject(i);

                int order_id = 0;
                int total_price = 0;
                String date = "";
                String status = "";
                try {
                    order_id = itemObject.getInt("id");
                    total_price = itemObject.getInt("total");
                    date = itemObject.getString("created_on");
                    status = itemObject.getString("status");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PastOrder newProduct = new PastOrder(order_id, total_price, date, status);
                pastOrders.add(newProduct);
            }
            return pastOrders;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String httpPostOrder(String uid, int items_price, int ship_fee, int total, String ship_name, String ship_phone, String ship_store_code, String ship_store_name, int ship_store_id, ArrayList<Product> products, String ship_email, String gcmToken) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", uid);
            jsonObject.put("items_price", Integer.toString(items_price));
            jsonObject.put("ship_fee", Integer.toString(ship_fee));
            jsonObject.put("total", Integer.toString(total));
            jsonObject.put("ship_name", ship_name);
            jsonObject.put("ship_phone", ship_phone);
            jsonObject.put("ship_email", ship_email);
            jsonObject.put("ship_store_code", ship_store_code);
            jsonObject.put("ship_store_id", Integer.toString(ship_store_id));
            jsonObject.put("ship_store_name", ship_store_name);
            jsonObject.put("registration_id", gcmToken);

            JSONArray productsArray = new JSONArray();
            for (int i = 0; i < products.size(); i++) {
                JSONObject productObject = new JSONObject();
                productObject.put("name", products.get(i).getName());
                productObject.put("quantity", Integer.toString(products.get(i).getBuy_count()));
                productObject.put("price", Integer.toString(products.get(i).getPrice()));
                productObject.put("style", products.get(i).getSelectedSpec().getStyle());
                productsArray.put(productObject);
            }
            jsonObject.put("products", productsArray);
            return post(UrlCenter.postOrder(), jsonObject.toString());
        } catch (Exception e) {
            Log.i("HTTP error", e.toString());
        }
        return "error";

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
