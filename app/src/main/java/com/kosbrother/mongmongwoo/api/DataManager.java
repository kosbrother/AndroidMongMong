package com.kosbrother.mongmongwoo.api;

import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.entity.AndroidVersionEntity;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.mynotification.MyNotification;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class DataManager {
    private static DataManager instance;
    private final NetworkAPI networkAPI;

    private DataManager() {
        OkHttpClient client;
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client = new OkHttpClient().newBuilder().addInterceptor(interceptor).build();
        } else {
            client = new OkHttpClient().newBuilder().build();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UrlCenter.HOST)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        networkAPI = retrofit.create(NetworkAPI.class);
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

    public interface NetworkAPI {
        @GET(UrlCenter.API + "/android_version")
        Observable<AndroidVersionEntity> getAndroidVersionObservable();

        @GET(UrlCenter.API_V3 + "/users/{userId}/my_messages")
        Observable<ResponseEntity<List<MyNotification>>> getMyMessages(@Path("userId") int userId);
    }
}
