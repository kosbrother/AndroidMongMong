package com.kosbrother.mongmongwoo.api;

import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.entity.AndroidVersionEntity;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.entity.UserEntity;
import com.kosbrother.mongmongwoo.entity.banner.Banner;
import com.kosbrother.mongmongwoo.entity.mycollect.FavoriteItemEntity;
import com.kosbrother.mongmongwoo.entity.mycollect.PostFavoriteItemsEntity;
import com.kosbrother.mongmongwoo.entity.mycollect.PostWishListsEntity;
import com.kosbrother.mongmongwoo.entity.mycollect.WishListEntity;
import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsCampaignsEntity;
import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsDetailEntity;
import com.kosbrother.mongmongwoo.entity.pastorder.PastOrder;
import com.kosbrother.mongmongwoo.entity.postorder.PostOrderResultEntity;
import com.kosbrother.mongmongwoo.entity.user.MmwUserEntity;
import com.kosbrother.mongmongwoo.model.Category;
import com.kosbrother.mongmongwoo.model.Order;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.mynotification.MyNotification;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DataManager {

    private static final String DEBUG_URL = "http://104.199.129.36/";
    private static final String PRD_URL = "https://www.mmwooo.com/";
    private static final String BASE_URL = BuildConfig.DEBUG ? DEBUG_URL : PRD_URL;

    private static DataManager instance;
    private final NetworkAPI networkAPI;
    private final Map<String, Subscription> subscriptionMap;

    private DataManager() {
        OkHttpClient client;
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();
        } else {
            client = new OkHttpClient().newBuilder().build();
        }

        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        networkAPI = retrofit.create(NetworkAPI.class);
        subscriptionMap = new LinkedHashMap<>();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void getAndroidVersion(final Action1<AndroidVersionEntity> action1) {
        Observable<AndroidVersionEntity> observable = networkAPI.getAndroidVersionObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<AndroidVersionEntity>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(AndroidVersionEntity androidVersionEntity) {
                action1.call(androidVersionEntity);
            }
        });
    }

    public void getMyNotificationList(int userId, final Action1<List<MyNotification>> action1) {
        Observable<List<MyNotification>> observable = networkAPI.getMyMessages(userId)
                .map(new Func1<ResponseEntity<List<MyNotification>>, List<MyNotification>>() {
                    @Override
                    public List<MyNotification> call(ResponseEntity<List<MyNotification>> listResponseEntity) {
                        List<MyNotification> data = listResponseEntity.getData();
                        if (data != null) {
                            return data;
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        observable.subscribe(new Observer<List<MyNotification>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<MyNotification> myNotifications) {
                if (myNotifications != null) {
                    action1.call(myNotifications);
                }
            }
        });

    }

    public void getFavoriteItems(int userId, final ApiCallBack callBack) {
        Observable<ResponseEntity<List<FavoriteItemEntity>>> observable = networkAPI.getFavoriteItems(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Observer<ResponseEntity<List<FavoriteItemEntity>>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<List<FavoriteItemEntity>> myCollectEntities) {
                List<FavoriteItemEntity> data = myCollectEntities.getData();
                if (data == null) {
                    onError(new Throwable(myCollectEntities.getError().getMessage()));
                } else {
                    callBack.onSuccess(data);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void postFavoriteItems(int userId, PostFavoriteItemsEntity postFavoriteItemsEntity
            , final ApiCallBack callBack) {
        Observable<ResponseEntity<String>> observable =
                networkAPI.postFavoriteItems(userId, postFavoriteItemsEntity)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Observer<ResponseEntity<String>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<String> listResponseEntity) {
                String result = listResponseEntity.getData();
                if (result == null) {
                    onError(new Throwable(listResponseEntity.getError().getMessage()));
                } else {
                    callBack.onSuccess(result);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void deleteFavoriteItems(int userId, int itemId, final ApiCallBack callBack) {
        Observable<ResponseEntity<String>> observable =
                networkAPI.deleteFavoriteItems(userId, itemId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Observer<ResponseEntity<String>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<String> listResponseEntity) {
                String result = listResponseEntity.getData();
                if (result == null) {
                    onError(new Throwable(listResponseEntity.getError().getMessage()));
                } else {
                    callBack.onSuccess(result);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void getWishLists(int userId, final ApiCallBack callBack) {
        Observable<ResponseEntity<List<WishListEntity>>> observable = networkAPI.getWishLists(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Observer<ResponseEntity<List<WishListEntity>>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<List<WishListEntity>> myCollectEntities) {
                List<WishListEntity> data = myCollectEntities.getData();
                if (data == null) {
                    onError(new Throwable(myCollectEntities.getError().getMessage()));
                } else {
                    callBack.onSuccess(data);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void postWishLists(int userId, PostWishListsEntity entity,
                              final ApiCallBack callBack) {
        Observable<ResponseEntity<String>> observable =
                networkAPI.postWishLists(userId, entity)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<String>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<String> stringResponseEntity) {
                String data = stringResponseEntity.getData();
                if (data == null) {
                    callBack.onError(stringResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void deleteWishListsItemSpecs(int userId, int itemSpecId, final ApiCallBack callBack) {
        Observable<ResponseEntity<String>> observable =
                networkAPI.deleteWishListsItemSpecs(userId, itemSpecId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Observer<ResponseEntity<String>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<String> stringResponseEntity) {
                String result = stringResponseEntity.getData();
                if (result == null) {
                    onError(new Throwable(stringResponseEntity.getError().getMessage()));
                } else {
                    callBack.onSuccess(result);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void getOrder(
            final int orderId, final ApiCallBack callBack) {
        Observable<ResponseEntity<PastOrder>> observable = networkAPI.getOrders(orderId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<PastOrder>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<PastOrder> pastOrderResponseEntity) {
                PastOrder data = pastOrderResponseEntity.getData();
                if (data == null) {
                    callBack.onError(pastOrderResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void cancelOrder(int userId, int pastOrderId, final ApiCallBack callBack) {
        Observable<ResponseEntity<String>> observable = networkAPI.cancelOrders(userId, pastOrderId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Observer<ResponseEntity<String>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<String> stringResponseEntity) {
                callBack.onSuccess(stringResponseEntity.getData());
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void postOrders(Order order, final ApiCallBack callBack) {
        Observable<ResponseEntity<PostOrderResultEntity>> observable = networkAPI.postOrders(order)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<PostOrderResultEntity>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<PostOrderResultEntity> postOrderResultEntityResponseEntity) {
                PostOrderResultEntity data = postOrderResultEntityResponseEntity.getData();
                if (data == null) {
                    onError(new Throwable("資料異常，請稍後再試．"));
                } else {
                    callBack.onSuccess(data);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void getSubCategories(String categoryName, final ApiCallBack callBack) {
        Observable<ResponseEntity<List<Category>>> observable =
                networkAPI.getSubcategories(categoryName)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<List<Category>>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<List<Category>> listResponseEntity) {
                List<Category> data = listResponseEntity.getData();
                if (data == null) {
                    callBack.onError(listResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void getCategorySortItems(String categoryName, String sortName, int page, final ApiCallBack callBack) {
        Observable<ResponseEntity<List<Product>>> observable =
                networkAPI.getCategorySortItems(categoryName, sortName, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<List<Product>>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<List<Product>> listResponseEntity) {
                List<Product> data = listResponseEntity.getData();
                if (data == null) {
                    callBack.onError(listResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void postOauthSessions(UserEntity user, final ApiCallBack callBack) {
        Observable<ResponseEntity<Integer>> observable =
                networkAPI.postOauthSessions(user)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<Integer>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<Integer> integerResponseEntity) {
                Integer data = integerResponseEntity.getData();
                if (data == null || data == 0) {
                    callBack.onError(integerResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void postMmwRegistrations(MmwUserEntity mmwUserEntity, final ApiCallBack callBack) {
        Observable<ResponseEntity<Integer>> observable =
                networkAPI.postMmwRegistrations(mmwUserEntity)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<Integer>>() {
            @Override
            public void onCompleted() {
                removeSubscription(key);
            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<Integer> integerResponseEntity) {
                Integer data = integerResponseEntity.getData();
                if (data == null || data == 0) {
                    callBack.onError(integerResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void getShoppingPointsInfo(int userId, final ApiCallBack callBack) {
        Observable<ResponseEntity<ShoppingPointsDetailEntity>> observable =
                networkAPI.getShoppingPointsInfo(userId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(
                new Subscriber<ResponseEntity<ShoppingPointsDetailEntity>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        removeSubscription(key);
                        callBack.onError(getErrorMessage(e));
                    }

                    @Override
                    public void onNext(ResponseEntity<ShoppingPointsDetailEntity> responseEntity) {
                        removeSubscription(key);
                        ShoppingPointsDetailEntity data = responseEntity.getData();
                        if (data == null) {
                            callBack.onError(responseEntity.getError().getMessage());
                        } else {
                            callBack.onSuccess(data);
                        }
                    }
                });
        subscriptionMap.put(key, subscription);
    }

    public void getShoppingPointsCampaign(int userId, final ApiCallBack callBack) {
        Observable<ResponseEntity<List<ShoppingPointsCampaignsEntity>>> observable =
                networkAPI.getShoppingPointCampaigns(userId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(
                new Subscriber<ResponseEntity<List<ShoppingPointsCampaignsEntity>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        removeSubscription(key);
                        callBack.onError(getErrorMessage(e));
                    }

                    @Override
                    public void onNext(ResponseEntity<List<ShoppingPointsCampaignsEntity>> responseEntity) {
                        removeSubscription(key);
                        List<ShoppingPointsCampaignsEntity> data = responseEntity.getData();
                        if (data == null) {
                            callBack.onError(responseEntity.getError().getMessage());
                        } else {
                            callBack.onSuccess(data);
                        }
                    }

                });
        subscriptionMap.put(key, subscription);
    }

    public void login(String email, String password, String registrationId, final ApiCallBack callBack) {
        Observable<ResponseEntity<Integer>> observable =
                networkAPI.postMmwRegistrationsLogin(email, password, registrationId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<Integer>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<Integer> integerResponseEntity) {
                Integer data = integerResponseEntity.getData();
                if (data == null) {
                    callBack.onError(integerResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
                removeSubscription(key);
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void forget(String email, final ApiCallBack callBack) {
        Observable<ResponseEntity<String>> observable =
                networkAPI.postMmwRegistrationsForget(email)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<String>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<String> stringResponseEntityResponseEntity) {
                String data = stringResponseEntityResponseEntity.getData();
                if (data == null) {
                    callBack.onError(stringResponseEntityResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
                removeSubscription(key);
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void getBanners(final ApiCallBack callBack) {
        Observable<ResponseEntity<List<Banner>>> observable =
                networkAPI.getBanners()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());

        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<List<Banner>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<List<Banner>> listResponseEntity) {
                List<Banner> data = listResponseEntity.getData();
                if (data == null) {
                    callBack.onError(listResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
                removeSubscription(key);
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void getProduct(int categoryId, int itemId, final ApiCallBack callBack) {
        Observable<ResponseEntity<Product>> observable = networkAPI.getProduct(categoryId, itemId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        final String key = String.valueOf(callBack.hashCode());
        Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<Product>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<Product> productResponseEntity) {
                Product data = productResponseEntity.getData();
                if (data == null) {
                    callBack.onError(productResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
                removeSubscription(key);
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void getProduct(String categoryName, String slug, final ApiCallBack callBack) {
        Observable<ResponseEntity<Product>> observable = networkAPI.getProduct(categoryName, slug)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
        final String key = String.valueOf(callBack.hashCode());
        final Subscription subscription = observable.subscribe(new Subscriber<ResponseEntity<Product>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                callBack.onError(getErrorMessage(e));
                removeSubscription(key);
            }

            @Override
            public void onNext(ResponseEntity<Product> productResponseEntity) {
                Product data = productResponseEntity.getData();
                if (data == null) {
                    callBack.onError(productResponseEntity.getError().getMessage());
                } else {
                    callBack.onSuccess(data);
                }
                removeSubscription(key);
            }
        });
        subscriptionMap.put(key, subscription);
    }

    public void unSubscribe(ApiCallBack callBack) {
        if (callBack == null) {
            return;
        }
        String key = String.valueOf(callBack.hashCode());
        synchronized (subscriptionMap) {
            Subscription subscription = subscriptionMap.get(key);
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
                subscriptionMap.remove(key);
            }
        }
    }

    public boolean isAllTasksDone() {
        return subscriptionMap.isEmpty();
    }

    private synchronized void removeSubscription(String key) {
        subscriptionMap.remove(key);
    }

    private String getErrorMessage(Throwable e) {
        String errorMessage = e.getMessage();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            if (code >= 400 && code < 500) {
                try {
                    errorMessage = ((HttpException) e).response().errorBody().string();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        return errorMessage;
    }

    public interface ApiCallBack {
        void onError(String errorMessage);

        void onSuccess(Object data);
    }

    public interface NetworkAPI {
        @GET("api/android_version")
        Observable<AndroidVersionEntity> getAndroidVersionObservable();

        @GET("api/v4/users/{userId}/my_messages")
        Observable<ResponseEntity<List<MyNotification>>> getMyMessages(@Path("userId") int userId);

        @GET("api/v3/users/{userId}/favorite_items")
        Observable<ResponseEntity<List<FavoriteItemEntity>>> getFavoriteItems(@Path("userId") int userId);

        @POST("api/v3/users/{userId}/favorite_items")
        Observable<ResponseEntity<String>> postFavoriteItems(
                @Path("userId") int userId, @Body PostFavoriteItemsEntity postFavoriteItemsEntity);

        @DELETE("api/v3/users/{userId}/favorite_items/items/{itemId}")
        Observable<ResponseEntity<String>> deleteFavoriteItems(
                @Path("userId") int userId, @Path("itemId") int itemId);

        @GET("api/v3/users/{userId}/wish_lists")
        Observable<ResponseEntity<List<WishListEntity>>> getWishLists(@Path("userId") int userId);

        @POST("api/v3/users/{userId}/wish_lists")
        Observable<ResponseEntity<String>> postWishLists(
                @Path("userId") int userId, @Body PostWishListsEntity postWishListsEntity);

        @DELETE("api/v3/users/{userId}/wish_lists/item_specs/{itemSpecId}")
        Observable<ResponseEntity<String>> deleteWishListsItemSpecs(
                @Path("userId") int userId, @Path("itemSpecId") int itemSpecId);

        @GET("api/v4/orders/{orderId}")
        Observable<ResponseEntity<PastOrder>> getOrders(@Path("orderId") int orderId);

        @PATCH("api/v3/users/{userId}/orders/{orderId}/cancel")
        Observable<ResponseEntity<String>> cancelOrders(
                @Path("userId") int userId, @Path("orderId") int orderId);

        @POST("api/v4/orders")
        Observable<ResponseEntity<PostOrderResultEntity>> postOrders(@Body Order order);

        @GET("api/v3/categories/{categoryName}/subcategory")
        Observable<ResponseEntity<List<Category>>> getSubcategories(@Path("categoryName") String categoryName);

        @GET("api/v3/categories/{categoryName}/items")
        Observable<ResponseEntity<List<Product>>> getCategorySortItems(
                @Path("categoryName") String categoryName, @Query("sort") String sortName, @Query("page") int page);

        @POST("api/v4/oauth_sessions")
        Observable<ResponseEntity<Integer>> postOauthSessions(@Body UserEntity userEntity);

        @POST("api/v4/mmw_registrations")
        Observable<ResponseEntity<Integer>> postMmwRegistrations(@Body MmwUserEntity mmwUserEntity);

        @GET("api/v4/users/{userId}/shopping_points")
        Observable<ResponseEntity<ShoppingPointsDetailEntity>> getShoppingPointsInfo(
                @Path("userId") int userId);

        @GET("api/v4/users/{userId}/shopping_point_campaigns")
        Observable<ResponseEntity<List<ShoppingPointsCampaignsEntity>>> getShoppingPointCampaigns(
                @Path("userId") int userId);

        @FormUrlEncoded
        @POST("api/v4/mmw_registrations/login")
        Observable<ResponseEntity<Integer>> postMmwRegistrationsLogin(
                @Field("email") String email, @Field("password") String password,
                @Field("registration_id") String registrationId);

        @FormUrlEncoded
        @POST("api/v3/mmw_registrations/forget")
        Observable<ResponseEntity<String>> postMmwRegistrationsForget(@Field("email") String email);

        @GET("api/v4/banners")
        Observable<ResponseEntity<List<Banner>>> getBanners();

        @GET("api/v3/categories/{categoryId}/items/{itemId}")
        Observable<ResponseEntity<Product>> getProduct(
                @Path("categoryId") int categoryId, @Path("itemId") int itemId);

        @GET("api/v3/categories/{categoryName}/items/{slug}")
        Observable<ResponseEntity<Product>> getProduct(
                @Path("categoryName") String categoryName, @Path("slug") String slug);
    }

}
