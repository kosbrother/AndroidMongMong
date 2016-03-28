package com.jasonko.mongmongwoo;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.jasonko.mongmongwoo.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kolichung on 3/9/16.
 */
public class ShoppingCarPreference {

    public static final String PREFS_NAME = "MONMON_APP";
    public static final String FAVORITES = "SHOPPING_CAR";


    public void storeShoppingItems(Context context, List favorites) {
    // used for store arrayList in json format
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);
        editor.putString(FAVORITES, jsonFavorites);
        editor.commit();
    }

    public ArrayList loadShoppingItems(Context context) {
    // used for retrieving arraylist from json formatted string
        SharedPreferences settings;
        List favorites;
        settings = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Product[] favoriteProducts = gson.fromJson(jsonFavorites,Product[].class);
            favorites = Arrays.asList(favoriteProducts);
            favorites = new ArrayList(favorites);
        } else
            return new ArrayList<Product>();
        return (ArrayList) favorites;
    }

    public void addShoppingItem(Context context, Product theProduct) {
        List favorites = loadShoppingItems(context);
        if (favorites == null)
            favorites = new ArrayList();
        favorites.add(theProduct);
        storeShoppingItems(context, favorites);
    }

    public void removeShoppingItem(Context context, int itemPostition) {
        ArrayList favorites = loadShoppingItems(context);
        if (favorites != null) {
            favorites.remove(itemPostition);
            storeShoppingItems(context, favorites);
        }
    }

    public void removeAllShoppingItems(Context context){
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.remove(FAVORITES);
        editor.commit();
    }


    public int getShoppingCarItemSize(Context context){
        ArrayList arrayList = loadShoppingItems(context);
        return arrayList.size();
    }
}
