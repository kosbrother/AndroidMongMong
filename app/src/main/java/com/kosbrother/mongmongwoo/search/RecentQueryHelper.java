package com.kosbrother.mongmongwoo.search;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class RecentQueryHelper {

    private static final String PREF_QUERY_HISTORY = "PREF_QUERY_HISTORY";
    private static final String KEY_QUERY_JSON_STRING = "KEY_QUERY_JSON_STRING";

    private static RecentQueryHelper INSTANCE = null;
    private final Context context;

    private RecentQueryHelper(Context context) {
        this.context = context;
    }

    public static RecentQueryHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new RecentQueryHelper(context);
        }
        return INSTANCE;
    }

    public void saveRecentQuery(String query) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_QUERY_HISTORY, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_QUERY_JSON_STRING, new Gson().toJson(new ArrayList<String>()));

        Gson gson = new Gson();
        List<String> queryList = gson.fromJson(jsonString, new TypeToken<List<String>>() {
        }.getType());
        if (queryList.contains(query)) {
            queryList.remove(query);
        }
        queryList.add(query);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_QUERY_JSON_STRING, gson.toJson(queryList));
        editor.apply();
    }

    public List<String> getQueryStringList() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_QUERY_HISTORY, Context.MODE_PRIVATE);
        String jsonString = prefs.getString(KEY_QUERY_JSON_STRING, new Gson().toJson(new ArrayList<String>()));

        return new Gson().fromJson(jsonString, new TypeToken<List<String>>() {
        }.getType());
    }

    public void clear() {
        context.getSharedPreferences(PREF_QUERY_HISTORY, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    public void removeQuery(String query) {
        List<String> list = getQueryStringList();
        if (list.remove(query)) {
            saveRecentQuery(list);
        }
    }

    private void saveRecentQuery(List<String> queryList) {
        context.getSharedPreferences(PREF_QUERY_HISTORY, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_QUERY_JSON_STRING, new Gson().toJson(queryList))
                .apply();
    }
}
