package com.kosbrother.mongmongwoo.googleanalytics;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.googleanalytics.event.GAEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.error.ErrorEvent;

public class GAManager {

    private static final String TAG = "GaManager";

    private static Tracker mTracker;
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void init(Context context) {
        initGA(context);
        initFA(context);
    }

    public static void sendEvent(GAEvent event) {
        sendGaEvent(event);
        sendFaEvent(event);
    }

    public static void sendException(GAEvent exceptionEvent) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, exceptionEvent.getAction() + ": " + exceptionEvent.getLabel());
        }
        sendGaEvent(exceptionEvent);
        if (mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString("message", exceptionEvent.getLabel());
            mFirebaseAnalytics.logEvent(exceptionEvent.getAction(), bundle);
        }
    }

    public static void sendError(String errorTitle, ResponseEntity.Error error) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, errorTitle + ": " + error.getMessage());
        }
        sendGaEvent(new ErrorEvent(errorTitle, error.getMessage()));
        if (mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString("message", error.getMessage());
            bundle.putString("code", "" + error.getCode());
            mFirebaseAnalytics.logEvent(errorTitle, bundle);
        }
    }

    private static void initGA(Context context) {
        // To enable debug logging use:
        // adb shell setprop log.tag.GAv4 DEBUG
        // adb logcat -s GAv4
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        if (BuildConfig.DEBUG) {
            analytics.setDryRun(true);
        }
        mTracker = analytics.newTracker("UA-73843935-2");
        mTracker.setSessionTimeout(300);
        mTracker.enableExceptionReporting(true);
        mTracker.enableAutoActivityTracking(true);
    }

    private static void initFA(Context context) {
        // You can enable verbose logging with a series of adb commands:
        // adb shell setprop log.tag.FA VERBOSE
        // adb shell setprop log.tag.FA-SVC VERBOSE
        // adb logcat -v time -s FA FA-SVC
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
    }

    private static void sendGaEvent(GAEvent event) {
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

    private static void sendFaEvent(GAEvent event) {
        if (mFirebaseAnalytics == null) {
            return;
        }
        // TODO: 2016/5/30 define new FA event
        Bundle bundle = new Bundle();
        bundle.putString("Category", event.getCategory());
        bundle.putString("Action", event.getAction());
        bundle.putString("Label", event.getLabel());
        mFirebaseAnalytics.logEvent("GAEvent", bundle);
    }

    public static void sendScreen(String screenName) {
        if (mTracker == null) {
            return;
        }
        mTracker.setScreenName(screenName);
        mTracker.send(new HitBuilders.ScreenViewBuilder()
                .build());
        // TODO: 2016/5/30 define new event
        Bundle bundle = new Bundle();
        bundle.putString("ScreenName", screenName);
        mFirebaseAnalytics.logEvent("ScreenName", bundle);
    }

}
