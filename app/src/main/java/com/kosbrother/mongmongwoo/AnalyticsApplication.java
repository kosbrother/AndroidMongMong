package com.kosbrother.mongmongwoo;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;

import io.fabric.sdk.android.Fabric;

public class AnalyticsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        GAManager.init(this);
        Settings.init(this);
        ShoppingCartManager.init(this);
        setCrashlyticsIfRelease();
    }

    private void setCrashlyticsIfRelease() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
        }
    }

}
