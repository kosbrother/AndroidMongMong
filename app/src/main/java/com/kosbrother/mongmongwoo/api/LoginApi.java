package com.kosbrother.mongmongwoo.api;

import java.io.IOException;

import okhttp3.FormBody;

public class LoginApi {

    public static String login(String email, String password) throws IOException {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("email", email);
        formBody.add("password", password);
        return RequestUtil.post(UrlCenter.login(), formBody.build());
    }

    public static String forget(String email) throws IOException {
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("email", email);
        return RequestUtil.post(UrlCenter.forget(), formBody.build());
    }
}
