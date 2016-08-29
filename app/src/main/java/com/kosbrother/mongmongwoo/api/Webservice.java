package com.kosbrother.mongmongwoo.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.entity.ShopInfoEntity;
import com.kosbrother.mongmongwoo.entity.postorder.PostOrder;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.exception.ExceptionEvent;
import com.kosbrother.mongmongwoo.model.Category;
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

    public static void getCategories(
            Action1<? super ResponseEntity<List<Category>>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(RequestUtil.get(UrlCenter.getCategories()));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<Category>>>() {
                    @Override
                    public ResponseEntity<List<Category>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<Category>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getCategoriesException");
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

    public static void getOrdersByEmailAndPhone(
            final String email, final String phone,
            Action1<? super ResponseEntity<List<PostOrder>>> onNextAction) {
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
                .map(new Func1<String, ResponseEntity<List<PostOrder>>>() {
                    @Override
                    public ResponseEntity<List<PostOrder>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<PostOrder>>>() {
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

    public static void getOrdersByEmail(
            final String email,
            Action1<? super ResponseEntity<List<PostOrder>>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(OrderApi.getOrdersByEmail(email));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<PostOrder>>>() {
                    @Override
                    public ResponseEntity<List<PostOrder>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<PostOrder>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getOrdersByEmailException");
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

    public static void getShopInfos(Action1<? super List<ShopInfoEntity>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(ShopInfoApi.getShopInfos());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<ShopInfoEntity>>>() {
                    @Override
                    public ResponseEntity<List<ShopInfoEntity>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<ShopInfoEntity>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .map(new Func1<ResponseEntity<List<ShopInfoEntity>>, List<ShopInfoEntity>>() {
                    @Override
                    public List<ShopInfoEntity> call(ResponseEntity<List<ShopInfoEntity>> listResponseEntity) {
                        List<ShopInfoEntity> data = listResponseEntity.getData();
                        if (data == null) {
                            handleError(listResponseEntity.getError(), "getFaqError");
                        }
                        return data;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getFaqException");
                    }
                });
    }

    public static void postRegistrationId(
            final String token,
            Action1<? super String> onNextAction) {
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
                .map(new Func1<ResponseEntity<String>, String>() {
                    @Override
                    public String call(ResponseEntity<String> stringResponseEntity) {
                        String data = stringResponseEntity.getData();
                        if (data == null) {
                            handleError(stringResponseEntity.getError(), "postRegistrationToServerError");
                        }
                        return data;
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

    public static void getSearchItems(
            final String query, final int page,
            Action1<? super List<Product>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(SearchApi.getSearchItems(query, page));
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
                .map(new Func1<ResponseEntity<List<Product>>, List<Product>>() {
                    @Override
                    public List<Product> call(ResponseEntity<List<Product>> listResponseEntity) {
                        List<Product> data = listResponseEntity.getData();
                        if (data == null) {
                            handleError(listResponseEntity.getError(), "getSearchItemsError");
                        }
                        return data;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getSearchItemsException");
                    }
                });
    }

    public static void getSuggestions(
            Action1<? super List<String>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(SearchApi.getSuggestions());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<String>>>() {
                    @Override
                    public ResponseEntity<List<String>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<String>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .map(new Func1<ResponseEntity<List<String>>, List<String>>() {
                    @Override
                    public List<String> call(ResponseEntity<List<String>> listResponseEntity) {
                        List<String> data = listResponseEntity.getData();
                        if (data == null) {
                            handleError(listResponseEntity.getError(), "getSuggestionsError");
                        }
                        return data;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getSuggestionsException");
                    }
                });
    }

    public static void getHotKeywords(
            Action1<? super List<String>> onNextAction) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(SearchApi.getHotKeywords());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .map(new Func1<String, ResponseEntity<List<String>>>() {
                    @Override
                    public ResponseEntity<List<String>> call(String json) {
                        Type listType = new TypeToken<ResponseEntity<List<String>>>() {
                        }.getType();
                        return new Gson().fromJson(json, listType);
                    }
                })
                .map(new Func1<ResponseEntity<List<String>>, List<String>>() {
                    @Override
                    public List<String> call(ResponseEntity<List<String>> listResponseEntity) {
                        List<String> data = listResponseEntity.getData();
                        if (data == null) {
                            handleError(listResponseEntity.getError(), "getHotKeywordsError");
                        }
                        return data;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNextAction, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        handleThrowable(throwable, "getHotKeywordsException");
                    }
                });
    }

    private static void handleThrowable(Throwable throwable, String exceptionTitle) {
        throwable.printStackTrace();
        GAManager.sendException(new ExceptionEvent(exceptionTitle, throwable.getMessage()));
    }

    private static void handleError(ResponseEntity.Error error, String errorTitle) {
        GAManager.sendError(errorTitle, error);
    }

}
