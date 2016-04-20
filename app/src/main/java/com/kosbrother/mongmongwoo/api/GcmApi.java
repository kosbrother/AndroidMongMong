package com.kosbrother.mongmongwoo.api;

import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GcmApi {

    public static final String TAG = "GCM_API";

    public static void postRegistrationId(String gcmId) {
        OkHttpClient client = new OkHttpClient();

        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("registration_id", gcmId);

        Request request = new Request.Builder()
                .url(UrlCenter.postRegistrationId())
                .post(formBody.build())
                .build();
        try {
            Response response = client.newCall(request).execute();
            Log.i(TAG, String.valueOf(response.isSuccessful()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
