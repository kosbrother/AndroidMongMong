package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by kolichung on 3/9/16.
 */
public class UserApi {

    public static final String  TAG   = "USER_API";
    public static final String host = "http://api.kosbrother.com";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String httpPostUser(String user_name, String real_name, String gender, String phone, String address, String fb_uid){
        String url = host + "/api/v1/users";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_name", user_name);
            jsonObject.put("real_name", real_name);
            jsonObject.put("gender", gender);
            jsonObject.put("phone", phone);
            jsonObject.put("address", address);
            jsonObject.put("uid", fb_uid);
            return post(url, jsonObject.toString());
        }catch (Exception e){
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
}
