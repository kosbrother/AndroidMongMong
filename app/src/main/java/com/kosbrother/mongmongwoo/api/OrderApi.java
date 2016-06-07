package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.model.PastOrder;

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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderApi {

    public static final String TAG = "ORDER_API";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static List<PastOrder> getOrdersByUid(String uid, int page) {
        String message = getMessageFromServer("GET", null, UrlCenter.getOrdersByUid(uid, page));
        if (message == null) {
            return null;
        } else {
            Type type = new TypeToken<List<PastOrder>>() {
            }.getType();
            return new Gson().fromJson(message, type);
        }
    }

    public static PastOrder getPastOrderByOrderId(int orderId) {
        String message = getMessageFromServer(
                "GET", null, UrlCenter.getPastOrderByOrderId(orderId));
        if (message == null) {
            return null;
        } else {
            return new Gson().fromJson(message, PastOrder.class);
        }
    }

    public static List<PastOrder> getOrdersByEmailAndPhone(String email, String phone) {
        String message = getMessageFromServer(
                "GET", null, UrlCenter.getOrdersByEmailAndPhone(email, phone));
        if (message == null) {
            return null;
        } else {
            Type type = new TypeToken<List<PastOrder>>() {
            }.getType();
            return new Gson().fromJson(message, type);
        }
    }

    public static String postOrder(String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(UrlCenter.postOrder())
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        if (DEBUG) {
            Log.i(TAG, request.url().toString());
            Log.i(TAG, json);
            Log.i(TAG, request.body().toString());
            Log.i(TAG, responseString);
        }
        return responseString;
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
