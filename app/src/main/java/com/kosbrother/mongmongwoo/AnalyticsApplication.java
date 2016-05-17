package com.kosbrother.mongmongwoo;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kosbrother.mongmongwoo.gcm.GcmPreferences;
import com.kosbrother.mongmongwoo.gcm.RegistrationIntentService;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;

public class AnalyticsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        GAManager.init(this);
        Settings.init(this);
        startGetGcmToken();
    }

    private void startGetGcmToken() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sentToken = sharedPreferences
                .getBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, false);
        String token = sharedPreferences
                .getString(GcmPreferences.TOKEN, "");
        if (!sentToken || token.isEmpty()) {
            // Start IntentService to register this application with GCM.
            startService(new Intent(this, RegistrationIntentService.class));
        }
    }

}
