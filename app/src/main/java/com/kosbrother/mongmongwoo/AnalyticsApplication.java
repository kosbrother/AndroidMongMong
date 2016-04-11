package com.kosbrother.mongmongwoo;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
        getGcmToken();
    }

    private void getGcmToken() {
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
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
