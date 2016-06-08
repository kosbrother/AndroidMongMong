package com.kosbrother.mongmongwoo.api;

import java.io.IOException;

public class AndroidVersionApi {

    public static String getVersion() throws IOException {
        return RequestUtil.get(UrlCenter.getAndroidVersion());
    }

}
