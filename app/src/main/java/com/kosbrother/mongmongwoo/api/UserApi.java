package com.kosbrother.mongmongwoo.api;

import android.util.Log;

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

    public static final String TAG = "USER_API";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static boolean postUser(String userJsonString) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, userJsonString);
        Request request = new Request.Builder()
                .url(UrlCenter.postUser())
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.i(TAG, response.body().string());
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
