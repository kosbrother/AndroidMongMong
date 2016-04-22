package com.kosbrother.mongmongwoo;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kosbrother.mongmongwoo.gcm.GcmPreferences;
import com.kosbrother.mongmongwoo.gcm.RegistrationIntentService;

/**
 * Created by kolichung on 3/31/16.
 */
public class AnalyticsApplication extends Application {
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(this);
        AppEventsLogger.activateApp(this);
        initGoogleAnalyticsTracker();
        startGetGcmToken();
    }

    private void initGoogleAnalyticsTracker() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (BuildConfig.DEBUG) {
            analytics.setDryRun(true);
        }
        mTracker = analytics.newTracker("UA-73843935-2");
        mTracker.setSessionTimeout(300);
        mTracker.enableExceptionReporting(true);
        mTracker.enableAutoActivityTracking(true);
    }

    private void startGetGcmToken() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean sentToken = sharedPreferences
                .getBoolean(GcmPreferences.SENT_TOKEN_TO_SERVER, false);
        if (!sentToken) {
            // Start IntentService to register this application with GCM.
            startService(new Intent(this, RegistrationIntentService.class));
        }
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        return mTracker;
    }
}
