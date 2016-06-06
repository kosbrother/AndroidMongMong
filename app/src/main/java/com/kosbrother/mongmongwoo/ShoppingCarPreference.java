package com.kosbrother.mongmongwoo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.ProductUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCarPreference {

    public static final String PREFS_NAME = "MONMON_APP";
    public static final String SHOPPING_CAR = "SHOPPING_CAR";

    public void storeShoppingItems(Context context, List favorites) {
        // used for store arrayList in json format
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString(SHOPPING_CAR, jsonFavorites);
        editor.apply();
    }

    public List<Product> loadShoppingItems(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String shoppingCartString = settings.getString(SHOPPING_CAR, "");
        if (shoppingCartString.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> productList;
        // amount is json key only in old version
        if (shoppingCartString.contains("amount")) {
            removeAllShoppingItems(context);
            productList = ProductUtil.getNewProductListFromOldProductString(shoppingCartString);
            storeShoppingItems(context, productList);
        } else {
            Type typeToken = new TypeToken<List<Product>>() {
            }.getType();
            Gson gson = new Gson();
            productList = gson.fromJson(shoppingCartString, typeToken);
        }
        return productList;
    }

    public void addShoppingItem(Context context, Product theProduct) {
        List<Product> favorites = loadShoppingItems(context);
        favorites.add(theProduct);
        storeShoppingItems(context, favorites);
    }

    public void removeShoppingItem(Context context, int itemPostition) {
        List favorites = loadShoppingItems(context);
        if (favorites != null) {
            favorites.remove(itemPostition);
            storeShoppingItems(context, favorites);
        }
    }

    public void removeAllShoppingItems(Context context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(SHOPPING_CAR)
                .apply();
    }

    public int getShoppingCarItemSize(Context context) {
        List arrayList = loadShoppingItems(context);
        return arrayList.size();
    }

}
