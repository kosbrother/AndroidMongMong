package com.kosbrother.mongmongwoo;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kosbrother.mongmongwoo.facebookevent.FacebookLogger;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;

import io.fabric.sdk.android.Fabric;

public class MongMongWooApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Context applicationContext = getApplicationContext();
        Settings.init(applicationContext);
        GAManager.init(applicationContext);
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
        FacebookLogger.init(applicationContext);
        ShoppingCartManager.init(applicationContext);
        if (!BuildConfig.DEBUG) {
            Fabric.with(applicationContext, new Crashlytics());
        }
    }
}
