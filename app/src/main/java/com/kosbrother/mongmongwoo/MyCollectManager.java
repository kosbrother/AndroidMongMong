package com.kosbrother.mongmongwoo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.model.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyCollectManager {

    private static final String PREF_MY_COLLECT = "PREF_MY_COLLECT";

    public static List<Product> getCollectedList(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                PREF_MY_COLLECT, Context.MODE_PRIVATE);
        List<Product> favorites = null;
        if (settings.contains(PREF_MY_COLLECT)) {
            String myCollectedJsonString = settings.getString(PREF_MY_COLLECT, "");
            Type listType = new TypeToken<List<Product>>() {
            }.getType();
            favorites = new Gson().fromJson(myCollectedJsonString, listType);
        }
        return favorites;
    }

    public static void addProductToCollectedList(Context context, Product theProduct) {
        List<Product> collectedProductList = getCollectedList(context);
        if (collectedProductList == null) {
            collectedProductList = new ArrayList<>();
        }
        collectedProductList.add(theProduct);
        storeCollectList(context, collectedProductList);
    }

    public static void removeProductFromCollectedList(Context context, Product product) {
        int removeProductId = product.getId();
        List<Product> collectedProductList = getCollectedList(context);
        for (int i = 0; i < collectedProductList.size(); i++) {
            if (collectedProductList.get(i).getId() == removeProductId) {
                collectedProductList.remove(i);
                storeCollectList(context, collectedProductList);
                break;
            }
        }
    }

    public static List<Product> removeProductFromCollectedList(Context context, int position) {
        List<Product> collectedProductList = getCollectedList(context);
        collectedProductList.remove(position);
        storeCollectList(context, collectedProductList);
        return collectedProductList;
    }

    private static void storeCollectList(Context context, List favorites) {
        String jsonFavorites = new Gson().toJson(favorites);

        SharedPreferences settings = context.getSharedPreferences(
                PREF_MY_COLLECT, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(PREF_MY_COLLECT, jsonFavorites);
        edit.apply();
    }
}
