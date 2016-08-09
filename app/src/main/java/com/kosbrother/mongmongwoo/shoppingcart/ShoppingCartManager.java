package com.kosbrother.mongmongwoo.shoppingcart;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.entity.postorder.UnableToBuyModel;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.ProductUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartManager {

    public static final String PREFS_NAME = "MONMON_APP";
    public static final String SHOPPING_CAR = "SHOPPING_CAR";

    private static ShoppingCartManager instance;
    private final SharedPreferences mPref;

    public ShoppingCartManager(Context context) {
        mPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new ShoppingCartManager(context);
        }
    }

    public static ShoppingCartManager getInstance() {
        return instance;
    }

    public void storeShoppingItems(List favorites) {
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(SHOPPING_CAR, jsonFavorites);
        editor.apply();
    }

    public List<Product> loadShoppingItems() {
        String shoppingCartString = mPref.getString(SHOPPING_CAR, "");
        if (shoppingCartString.isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> productList;
        if (ProductUtil.isOldProduct(shoppingCartString)) {
            removeAllShoppingItems();
            productList = ProductUtil.getNewProductListFromOldProductString(shoppingCartString);
            storeShoppingItems(productList);
        } else {
            Type typeToken = new TypeToken<List<Product>>() {
            }.getType();
            Gson gson = new Gson();
            productList = gson.fromJson(shoppingCartString, typeToken);
        }
        return productList;
    }

    public void addShoppingItem(Product theProduct) {
        List<Product> favorites = loadShoppingItems();
        favorites.add(theProduct);
        storeShoppingItems(favorites);
    }

    public void removeShoppingItem(int itemPostition) {
        List favorites = loadShoppingItems();
        if (favorites != null) {
            favorites.remove(itemPostition);
            storeShoppingItems(favorites);
        }
    }

    public void removeAllShoppingItems() {
        mPref.edit()
                .remove(SHOPPING_CAR)
                .apply();
    }

    public int getShoppingCarItemSize() {
        List arrayList = loadShoppingItems();
        return arrayList.size();
    }

    public List<Product> removeUnableToBuyFromShoppingCart(List<UnableToBuyModel> unableToBuyModels) {
        List<Product> wishProducts = new ArrayList<>();
        List<Product> shoppingItems = loadShoppingItems();
        for (UnableToBuyModel unableToBuy : unableToBuyModels) {
            int stockAmount = unableToBuy.getSpecStockAmount();
            boolean offShelf = unableToBuy.isOffShelf();
            if (offShelf || stockAmount == 0) {
                int specId = unableToBuy.getSpecId();
                for (int i = 0; i < shoppingItems.size(); i++) {
                    if (shoppingItems.get(i).getSelectedSpec().getId() == specId) {
                        wishProducts.add(shoppingItems.remove(i));
                        break;
                    }
                }
            } else {
                int specId = unableToBuy.getSpecId();
                for (int i = 0; i < shoppingItems.size(); i++) {
                    Product product = shoppingItems.get(i);
                    if (product.getSelectedSpec().getId() == specId) {
                        product.setBuy_count(stockAmount);
                        wishProducts.add(product);
                        break;
                    }
                }
            }
        }
        storeShoppingItems(shoppingItems);
        return wishProducts;
    }
}
