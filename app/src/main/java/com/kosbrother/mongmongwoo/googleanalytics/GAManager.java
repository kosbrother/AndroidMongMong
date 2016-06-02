package com.kosbrother.mongmongwoo.googleanalytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;

public class GAManager {
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void init(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
//        You can enable verbose logging with a series of adb commands:
//        adb shell setprop log.tag.FA VERBOSE
//        adb shell setprop log.tag.FA-SVC VERBOSE
//        adb logcat -v time -s FA FA-SVC
    }

    public static void sendEvent(GAEvent event) {
        // TODO: 2016/5/30 define new event
        Bundle bundle = new Bundle();
        bundle.putString("Category", event.getCategory());
        bundle.putString("Action", event.getAction());
        bundle.putString("Label", event.getLabel());
        mFirebaseAnalytics.logEvent("GAEvent", bundle);
    }

    public static void sendScreen(String screenName) {
        // TODO: 2016/5/30 define new event
        Bundle bundle = new Bundle();
        bundle.putString("ScreenName", screenName);
        mFirebaseAnalytics.logEvent("ScreenName", bundle);
    }
}
