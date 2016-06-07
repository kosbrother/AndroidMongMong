package com.kosbrother.mongmongwoo.api;

import java.io.IOException;

import okhttp3.FormBody;

public class GcmApi {

    public static final String TAG = "GCM_API";

    public static String postRegistrationId(String gcmId) throws IOException {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("registration_id", gcmId);
        return RequestUtil.post(UrlCenter.postRegistrationId(), formBody.build());
    }
}
