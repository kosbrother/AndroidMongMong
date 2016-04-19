package com.kosbrother.mongmongwoo.api;

import com.kosbrother.mongmongwoo.model.PastOrder;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Road;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.Town;

import java.util.ArrayList;

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

    public static void getPastOrdersByFbUid(
            final String fbUid, final int page, Action1<ArrayList<PastOrder>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<ArrayList<PastOrder>>() {
            @Override
            public void call(Subscriber<? super ArrayList<PastOrder>> subscriber) {
                subscriber.onNext(OrderApi.getPastOrdersByUid(fbUid, page));
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction);
    }

    public static void getPastOrderByOrderId(
            final PastOrder postOrder, Action1<? super PastOrder> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(OrderApi.getMessageFromServer(
                        "GET", null, null, UrlCenter.getPastOrderByOrderId(postOrder.getOrder_id())));
                subscriber.onCompleted();
            }
        })
                .map(new Func1<String, PastOrder>() {
                    @Override
                    public PastOrder call(String message) {
                        if (message == null) {
                            return null;
                        }
                        return OrderApi.parseTheOrder(message, postOrder);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction);
    }

    public static void getTowns(final int countyId, Action1<? super ArrayList<Town>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(StoreApi.getMessageFromServer(
                        "GET", null, null, UrlCenter.getTowns(countyId)));
                subscriber.onCompleted();
            }
        })
                .map(new Func1<String, ArrayList<Town>>() {
                    @Override
                    public ArrayList<Town> call(String message) {
                        if (message == null) {
                            return null;
                        }
                        ArrayList<Town> towns = new ArrayList<>();
                        return StoreApi.parseTowns(towns, message);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction);
    }

    public static void getRoads(
            final int countyId, final int townId, Action1<? super ArrayList<Road>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(StoreApi.getMessageFromServer
                        ("GET", null, null, UrlCenter.getRoads(countyId, townId)));
                subscriber.onCompleted();
            }
        })
                .map(new Func1<String, ArrayList<Road>>() {
                    @Override
                    public ArrayList<Road> call(String message) {
                        if (message == null) {
                            return null;
                        }
                        ArrayList<Road> roads = new ArrayList<>();
                        return StoreApi.parseRoads(roads, message);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction);
    }

    public static void getStores(
            final int county_id, final int town_id, final int road_id,
            Action1<? super ArrayList<Store>> onNextAction) {

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(StoreApi.getMessageFromServer(
                        "GET", null, null, UrlCenter.getStores(county_id, town_id, road_id)));
                subscriber.onCompleted();
            }
        })
                .map(new Func1<String, ArrayList<Store>>() {
                    @Override
                    public ArrayList<Store> call(String message) {
                        if (message == null) {
                            return null;
                        }
                        ArrayList<Store> roads = new ArrayList<>();
                        return StoreApi.parseStore(roads, message);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction);
    }
}