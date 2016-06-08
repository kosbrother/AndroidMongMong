package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import com.kosbrother.mongmongwoo.BuildConfig;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestUtil {
    private static final String TAG = "RequestUtil";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String get(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }
        String responseString = response.body().string();
        logRequestInfoIfDebug(url, response.headers(), responseString);
        return responseString;
    }

    public static String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        logRequestInfoIfDebug(url, json, response.headers(), responseString);
        return responseString;
    }

    public static String post(String url, FormBody body) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < body.size(); i++) {
            stringBuilder.append("\"");
            stringBuilder.append(body.name(i));
            stringBuilder.append("\"");
            stringBuilder.append(":");
            stringBuilder.append("\"");
            stringBuilder.append(body.value(i));
            stringBuilder.append("\"");
        }
        logRequestInfoIfDebug(url, stringBuilder.toString(), response.headers(), responseString);
        return responseString;
    }

    private static void logRequestInfoIfDebug(String url, Headers responseHeaders, String responseString) {
        if (BuildConfig.DEBUG) {
            for (int i = 0; i < responseHeaders.size(); i++) {
                Log.d(TAG, "Headers: " + responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
            Log.d(TAG, "url: " + url);
            Log.d(TAG, "response: " + responseString);
        }
    }

    private static void logRequestInfoIfDebug(String url, String postJson, Headers responseHeaders, String responseString) {
        if (BuildConfig.DEBUG) {
            for (int i = 0; i < responseHeaders.size(); i++) {
                Log.d(TAG, "Headers: " + responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }
            Log.d(TAG, "url: " + url);
            Log.d(TAG, "post: " + postJson);
            Log.d(TAG, "response: " + responseString);
        }
    }
}
