package com.kosbrother.mongmongwoo.googleanalytics;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class GAManager {
    private static Tracker mTracker;

    public static void init(Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        // To enable debug logging use:
        // adb shell setprop log.tag.GAv4 DEBUG
        // adb logcat -s GAv4
        if (BuildConfig.DEBUG) {
            analytics.setDryRun(true);
        }
        mTracker = analytics.newTracker("UA-73843935-2");
        mTracker.setSessionTimeout(300);
        mTracker.enableExceptionReporting(true);
        mTracker.enableAutoActivityTracking(true);
    }

    public static void sendEvent(GAEvent event) {
        if (mTracker == null) {
            return;
        }
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(event.getCategory())
                .setAction(event.getAction())
                .setLabel(event.getLabel())
                .setValue(event.getValue())
                .build());
    }

    public static void sendScreen(String screenName) {
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder()
                .build());
    }
}
