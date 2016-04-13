package com.kosbrother.mongmongwoo.api;

import com.kosbrother.mongmongwoo.model.Product;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class WebService {

    public static void postUser(final String userJsonString, Action1<? super Boolean> onNextAction) {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                subscriber.onNext(UserApi.postUser(userJsonString));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction);
    }

    public static void updateProductById(final Product product, Action1<Product> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(ProductApi.getMessageFromServer(
                        "GET", null, null, UrlCenter.updateProductById(product.getId())));
                subscriber.onCompleted();
            }
        })
                .map(new Func1<String, Product>() {
                    @Override
                    public Product call(String message) {
                        if (message == null) {
                            return null;
                        }
                        return ProductApi.parseItem(message, product);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction);
    }

}