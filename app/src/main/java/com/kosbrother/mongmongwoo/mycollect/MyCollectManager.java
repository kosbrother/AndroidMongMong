package com.kosbrother.mongmongwoo.mycollect;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.model.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyCollectManager {

    private static final String PREF_MY_COLLECT = "PREF_MY_COLLECT";
    private static final String PREF_STRING_COLLECT_LIST = "PREF_STRING_COLLECT_LIST";

    public static List<Product> getCollectedList(Context context) {
        SharedPreferences settings = context.getSharedPreferences(
                PREF_MY_COLLECT, Context.MODE_PRIVATE);
        String myCollectedJsonString = settings.getString(PREF_STRING_COLLECT_LIST, "");
        if (myCollectedJsonString.isEmpty()) {
            return new ArrayList<>();
        }
        Type listType = new TypeToken<List<Product>>() {
        }.getType();
        List<Product> productList = new Gson().fromJson(myCollectedJsonString, listType);
        // To fix null product bug
        removeAllNullFromListThenSave(context, productList);
        return productList;
    }

    public static void addProductToCollectedList(Context context, Product theProduct) {
        List<Product> collectedProductList = getCollectedList(context);
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
        List<Product> collectedList = getCollectedList(context);
        collectedList.remove(position);
        storeCollectList(context, collectedList);
        return collectedList;
    }

    private static void storeCollectList(Context context, List collectList) {
        String collectListString = new Gson().toJson(collectList);

        SharedPreferences settings = context.getSharedPreferences(
                PREF_MY_COLLECT, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(PREF_STRING_COLLECT_LIST, collectListString);
        edit.apply();
    }

    private static void removeAllNullFromListThenSave(Context context, List<Product> productList) {
        removeAllNullFromList(productList);
        storeCollectList(context, productList);
    }

    @VisibleForTesting
    static void removeAllNullFromList(List<Product> productList) {
        for (Iterator it = productList.iterator(); it.hasNext(); ) {
            Product product = (Product) it.next();
            if (product == null) {
                it.remove();
            }
        }
    }

}
