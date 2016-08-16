package com.kosbrother.mongmongwoo.utils;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.facebookevent.FacebookLogger;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;

import io.fabric.sdk.android.Fabric;

public class InitUtil {

    public static void initApp(Context applicationContext, Application application) {
        Settings.init(applicationContext);
        GAManager.init(applicationContext);
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(application);
        FacebookLogger.init(applicationContext);
        ShoppingCartManager.init(applicationContext);
        if (!BuildConfig.DEBUG) {
            Fabric.with(applicationContext, new Crashlytics());
        }
    }
}
