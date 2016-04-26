package com.kosbrother.mongmongwoo.api;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AndroidVersionApi {

    public static String getVersion() {
        String resultString = null;
        Request request = new Request.Builder()
                .url(UrlCenter.getAndroidVersion())
                .build();
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                resultString = response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultString;
    }

}
