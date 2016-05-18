package com.kosbrother.mongmongwoo.api;

import com.google.gson.Gson;
import com.kosbrother.mongmongwoo.entity.AndroidVersionEntity;
import com.kosbrother.mongmongwoo.model.PastOrder;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Webservice {
    public static final String TAG = "Webservice";

    public static void getAndroidVersion(Action1<? super AndroidVersionEntity> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(AndroidVersionApi.getVersion());
                subscriber.onCompleted();
            }
        })
                .map(new Func1<String, AndroidVersionEntity>() {
                    @Override
                    public AndroidVersionEntity call(String json) {
                        if (json != null) {
                            return new Gson().fromJson(json, AndroidVersionEntity.class);
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction);
    }

    public static void getOrdersByEmailAndPhone(
            final String email, final String phone, Action1<? super List<PastOrder>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<List<PastOrder>>() {
            @Override
            public void call(Subscriber<? super List<PastOrder>> subscriber) {
                subscriber.onNext(OrderApi.getOrdersByEmailAndPhone(email, phone));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction);
    }
}
