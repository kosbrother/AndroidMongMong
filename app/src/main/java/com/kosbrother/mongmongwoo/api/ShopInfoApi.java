package com.kosbrother.mongmongwoo.api;

import java.io.IOException;

public class ShopInfoApi {

    public static String getShopInfos() throws IOException {
        return RequestUtil.get(UrlCenter.getShopInfos());
    }
}
