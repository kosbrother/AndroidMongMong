package com.kosbrother.mongmongwoo.mycollect;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.ProductUtil;

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
        List<Product> productList;
        if (ProductUtil.isOldProduct(myCollectedJsonString)) {
            removeAllCollectList(context);
            productList = ProductUtil.getNewProductListFromOldProductString(myCollectedJsonString);
            storeCollectList(context, productList);
        } else {
            Type typeToken = new TypeToken<List<Product>>() {
            }.getType();
            Gson gson = new Gson();
            productList = gson.fromJson(myCollectedJsonString, typeToken);
        }
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

    public static void removeAllCollectList(Context context) {
        context.getSharedPreferences(PREF_MY_COLLECT, Context.MODE_PRIVATE)
                .edit()
                .remove(PREF_STRING_COLLECT_LIST)
                .apply();
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
