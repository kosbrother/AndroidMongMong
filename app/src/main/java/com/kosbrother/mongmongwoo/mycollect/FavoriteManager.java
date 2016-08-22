package com.kosbrother.mongmongwoo.mycollect;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.mycollect.FavoriteItemEntity;
import com.kosbrother.mongmongwoo.entity.mycollect.PostFavoriteItemsEntity;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.ProductUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FavoriteManager {

    private static final String PREF_MY_COLLECT = "PREF_MY_COLLECT";
    private static final String PREF_STRING_COLLECT_LIST = "PREF_STRING_COLLECT_LIST";

    private static FavoriteManager instance;
    private final List<Product> tmpProductList;
    private int userId;
    private Context context;

    private FavoriteManager(int userId, Context context) {
        this.userId = userId;
        this.context = context;
        tmpProductList = new ArrayList<>();
    }

    public static FavoriteManager getInstance(int userId, Context context) {
        if (instance == null) {
            instance = new FavoriteManager(userId, context);
        }
        return instance;
    }

    public void isProductCollected(final int productId, final DataManager.ApiCallBack callBack) {
        final List<Product> prefCollectedList = getCollectedList(context);
        // check old version collect list, if size more then 1, upload to server.
        if (prefCollectedList.size() > 0) {
            ArrayList<PostFavoriteItemsEntity.FavoriteItemEntity> favoriteItems = new ArrayList<>();
            for (Product p : prefCollectedList) {
                favoriteItems.add(new PostFavoriteItemsEntity.FavoriteItemEntity(p.getId()));
            }
            PostFavoriteItemsEntity entity = new PostFavoriteItemsEntity(favoriteItems);
            DataManager.getInstance().postFavoriteItems(userId, entity, new DataManager.ApiCallBack() {
                @Override
                public void onError(String errorMessage) {
                    callBack.onError(errorMessage);
                }

                @Override
                public void onSuccess(Object data) {
                    tmpProductList.addAll(prefCollectedList);
                    removeAllCollectList(context);
                    callBack.onSuccess(checkCollected(productId));
                }
            });
            return;
        }

        if (tmpProductList.isEmpty()) {
            DataManager.getInstance().getFavoriteItems(userId, new DataManager.ApiCallBack() {
                @Override
                public void onError(String errorMessage) {
                    callBack.onError(errorMessage);
                }

                @Override
                public void onSuccess(Object data) {
                    List<FavoriteItemEntity> list = (List<FavoriteItemEntity>) data;
                    for (FavoriteItemEntity entity : list) {
                        tmpProductList.add(entity.getProduct());
                    }
                    callBack.onSuccess(checkCollected(productId));
                }
            });
        } else {
            callBack.onSuccess(checkCollected(productId));
        }
    }

    public void deleteFavoriteItemsFromPosition(final int position, final DataManager.ApiCallBack callBack) {
        final int productId = tmpProductList.get(position).getId();
        deleteFavoriteItemsFromId(productId, callBack);
    }

    public void deleteFavoriteItemsFromId(final int productId, final DataManager.ApiCallBack callBack) {
        DataManager.getInstance().deleteFavoriteItems(userId, productId,
                new DataManager.ApiCallBack() {
                    @Override
                    public void onError(String errorMessage) {
                        callBack.onError(errorMessage);
                    }

                    @Override
                    public void onSuccess(Object data) {
                        for (Product p : tmpProductList) {
                            if (p.getId() == productId) {
                                tmpProductList.remove(p);
                                break;
                            }
                        }
                        callBack.onSuccess(data);
                    }
                }

        );
    }

    public void addFavoriteItems(final Product product, final DataManager.ApiCallBack callBack) {
        ArrayList<PostFavoriteItemsEntity.FavoriteItemEntity> list = new ArrayList<>();
        list.add(new PostFavoriteItemsEntity.FavoriteItemEntity(product.getId()));
        PostFavoriteItemsEntity postFavoriteItem = new PostFavoriteItemsEntity(list);

        DataManager.getInstance().postFavoriteItems(Settings.getSavedUser().getUserId(), postFavoriteItem,
                new DataManager.ApiCallBack() {
                    @Override
                    public void onError(String errorMessage) {
                        callBack.onError(errorMessage);
                    }

                    @Override
                    public void onSuccess(Object data) {
                        tmpProductList.add(product);
                        callBack.onSuccess(data);
                    }
                }
        );
    }

    public void getMyCollectList(final DataManager.ApiCallBack callBack) {
        List<Product> prefCollectedList = getCollectedList(context);
        // check old version collect list, if size more then 1, upload to server.
        if (prefCollectedList.size() > 0) {
            ArrayList<PostFavoriteItemsEntity.FavoriteItemEntity> favoriteItems = new ArrayList<>();
            for (Product p : prefCollectedList) {
                favoriteItems.add(new PostFavoriteItemsEntity.FavoriteItemEntity(p.getId()));
            }
            PostFavoriteItemsEntity entity = new PostFavoriteItemsEntity(favoriteItems);
            DataManager.getInstance().postFavoriteItems(userId, entity, new DataManager.ApiCallBack() {
                @Override
                public void onError(String errorMessage) {
                    callBack.onError(errorMessage);
                }

                @Override
                public void onSuccess(Object data) {
                    removeAllCollectList(context);
                    DataManager.getInstance().getFavoriteItems(userId, new DataManager.ApiCallBack() {
                        @Override
                        public void onError(String errorMessage) {
                            callBack.onError(errorMessage);
                        }

                        @Override
                        public void onSuccess(Object data) {
                            List<FavoriteItemEntity> list = (List<FavoriteItemEntity>) data;
                            for (FavoriteItemEntity entity : list) {
                                tmpProductList.add(entity.getProduct());
                            }
                            callBack.onSuccess(data);
                        }
                    });
                }
            });
            return;
        }

        if (tmpProductList.size() == 0) {
            DataManager.getInstance().getFavoriteItems(userId, new DataManager.ApiCallBack() {
                @Override
                public void onError(String errorMessage) {
                    callBack.onError(errorMessage);
                }

                @Override
                public void onSuccess(Object data) {
                    List<FavoriteItemEntity> list = (List<FavoriteItemEntity>) data;
                    for (FavoriteItemEntity entity : list) {
                        tmpProductList.add(entity.getProduct());
                    }
                    callBack.onSuccess(data);
                }
            });
        } else {
            callBack.onSuccess(tmpProductList);
        }
    }

    public List<Product> getProductList() {
        return tmpProductList;
    }

    public int getProductId(int position) {
        return tmpProductList.get(position).getId();
    }

    private boolean checkCollected(int productId) {
        for (Product p : tmpProductList) {
            if (p.getId() == productId) {
                return true;
            }
        }
        return false;
    }

    private List<Product> getCollectedList(Context context) {
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
        for (Iterator it = productList.iterator(); it.hasNext(); ) {
            Product product = (Product) it.next();
            if (product == null) {
                it.remove();
            }
        }
        return productList;
    }

    private static void storeCollectList(Context context, List collectList) {
        String collectListString = new Gson().toJson(collectList);

        SharedPreferences settings = context.getSharedPreferences(
                PREF_MY_COLLECT, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = settings.edit();
        edit.putString(PREF_STRING_COLLECT_LIST, collectListString);
        edit.apply();
    }

    private void removeAllCollectList(Context context) {
        context.getSharedPreferences(PREF_MY_COLLECT, Context.MODE_PRIVATE)
                .edit()
                .remove(PREF_STRING_COLLECT_LIST)
                .apply();
    }

    public void cancelRequest(DataManager.ApiCallBack callBack) {
        DataManager.getInstance().unSubscribe(callBack);
    }
}
