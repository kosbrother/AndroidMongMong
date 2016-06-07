package com.kosbrother.mongmongwoo.api;

import java.io.IOException;

public class UserApi {

    public static String postUser(String userJsonString) throws IOException {
        return RequestUtil.post(UrlCenter.postUser(), userJsonString);
    }
}
