package com.kosbrother.mongmongwoo.api;

import android.support.annotation.NonNull;
import android.util.Log;

import com.kosbrother.mongmongwoo.BuildConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestUtil {
    private static final String TAG = "RequestUtil";

    public static String get(String url) throws IOException {
        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            String responseString = getResponseString(response);
            logRequestInfoIfDebug(url, response.headers(), responseString);
            throw new IOException("Unexpected code " + response);
        }
        String responseString = getResponseString(response);
        logRequestInfoIfDebug(url, response.headers(), responseString);
        return responseString;
    }

    public static String post(String url, FormBody body) throws IOException {
        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        InputStream inputStream = response.body().byteStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString();
    }

    @NonNull
    private static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        HostnameVerifier hv =
                                HttpsURLConnection.getDefaultHostnameVerifier();
                        return hv.verify("mmwooo.com", session);
                    }
                }).build();
    }

    @NonNull
    private static String getResponseString(Response response) throws IOException {
        InputStream inputStream = response.body().byteStream();
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line).append('\n');
        }
        return total.toString();
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

}
