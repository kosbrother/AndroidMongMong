package com.kosbrother.mongmongwoo.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.entity.AndroidVersionEntity;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.exception.ExceptionEvent;
import com.kosbrother.mongmongwoo.model.PastOrder;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Road;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.Town;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Webservice {
    public static final String TAG = "Webservice";

    public static void getAndroidVersion(
            Action1<? super AndroidVersionEntity> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(AndroidVersionApi.getVersion());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, AndroidVersionEntity>() {
                    @Override
                    public AndroidVersionEntity call(String json) {
                        return new Gson().fromJson(json, AndroidVersionEntity.class);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "checkAndroidVersionException");
                    }
                });
    }

    public static void getProduct(
            final int categoryId, final int productId,
            Action1<? super ResponseEntity<Product>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(ProductApi.getProduct(categoryId, productId));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<Product>>() {
                    @Override
                    public ResponseEntity<Product> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<Product>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getProductException");
                    }
                });
    }

    public static void getProduct(
            final String categoryName, final String slug,
            Action1<? super ResponseEntity<Product>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(ProductApi.getProduct(categoryName, slug));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<Product>>() {
                    @Override
                    public ResponseEntity<Product> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<Product>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getProductException");
                    }
                });
    }

    public static void getProducts(
            final int categoryId, final int page,
            Action1<? super ResponseEntity<List<Product>>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(ProductApi.getCategoryProducts(categoryId, page));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<Product>>>() {
                    @Override
                    public ResponseEntity<List<Product>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<Product>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getProductsException");
                    }
                });
    }

    public static void getOrdersByEmailAndPhone(
            final String email, final String phone,
            Action1<? super ResponseEntity<List<PastOrder>>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(OrderApi.getOrdersByEmailAndPhone(email, phone));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<PastOrder>>>() {
                    @Override
                    public ResponseEntity<List<PastOrder>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<PastOrder>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getOrdersByEmailAndPhoneException");
                    }
                });
    }

    public static void getOrdersByUid(
            final String uid, final int page,
            Action1<? super ResponseEntity<List<PastOrder>>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(OrderApi.getOrdersByUid(uid, page));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<PastOrder>>>() {
                    @Override
                    public ResponseEntity<List<PastOrder>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<PastOrder>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getOrdersByUidException");
                    }
                });
    }

    public static void getPastOrderByOrderId(
            final int orderId,
            Action1<? super ResponseEntity<PastOrder>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(OrderApi.getPastOrderByOrderId(orderId));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<PastOrder>>() {
                    @Override
                    public ResponseEntity<PastOrder> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<PastOrder>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getPastOrderByOrderIdException");
                    }
                });
    }

    public static void getTowns(
            final int countryId,
            Action1<? super ResponseEntity<List<Town>>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(StoreApi.getTowns(countryId));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<Town>>>() {
                    @Override
                    public ResponseEntity<List<Town>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<Town>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getTownsException");
                    }
                });
    }

    public static void getRoads(
            final int countryId, final int townId,
            Action1<? super ResponseEntity<List<Road>>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(StoreApi.getRoads(countryId, townId));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<Road>>>() {
                    @Override
                    public ResponseEntity<List<Road>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<Road>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getRoadsException");
                    }
                });
    }

    public static void getStores(
            final int countryId, final int townId, final int roadId,
            Action1<? super ResponseEntity<List<Store>>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(StoreApi.getStores(countryId, townId, roadId));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<Store>>>() {
                    @Override
                    public ResponseEntity<List<Store>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<Store>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getStoresException");
                    }
                });
    }

    public static void postRegistrationId(
            final String token,
            Action1<? super ResponseEntity<String>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(GcmApi.postRegistrationId(token));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<String>>() {
                    @Override
                    public ResponseEntity<String> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<String>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "postRegistrationIdException");
                    }
                });
    }

    public static void postUser(
            final String userJsonString,
            Action1<? super ResponseEntity<String>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(UserApi.postUser(userJsonString));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<String>>() {
                    @Override
                    public ResponseEntity<String> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<String>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "postUserException");
                    }
                });
    }

    public static void postOrder(
            final String orderJsonString,
            Action1<? super ResponseEntity<PastOrder>> onNextAction,
            final Action1<IOException> onWebserviceExceptionAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(OrderApi.postOrder(orderJsonString));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<PastOrder>>() {
                    @Override
                    public ResponseEntity<PastOrder> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<PastOrder>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof IOException) {
                            onWebserviceExceptionAction.call((IOException) throwable);
                        }else {
                            handleThrowable(throwable, "postOrderException");
                        }
                    }
                });
    }

    private static void handleThrowable(Throwable throwable, String exceptionTitle) {
        throwable.printStackTrace();
        GAManager.sendException(new ExceptionEvent(exceptionTitle, throwable.getMessage()));
    }
}
