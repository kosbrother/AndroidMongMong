package com.kosbrother.mongmongwoo.api;

import java.io.IOException;

public class SearchApi {
    static String getSearchItems(String query, int page) throws IOException {
        return RequestUtil.get(UrlCenter.searchItems(query, page));
    }

    static String getHotKeywords() throws IOException {
        return RequestUtil.get(UrlCenter.getHotKeywords());
    }

    static String getSuggestions() throws IOException {
        return RequestUtil.get(UrlCenter.getSuggestions());
    }
}
