package com.kosbrother.mongmongwoo.facebookevent;

import android.content.Context;
import android.os.Bundle;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.kosbrother.mongmongwoo.BuildConfig;

import java.math.BigDecimal;
import java.util.Currency;

public class FacebookLogger {

    private static final String CURRENCY_STRING = "TWD";

    private static FacebookLogger instance;
    private static AppEventsLogger logger;

    private FacebookLogger() {
    }

    public static void init(Context context) {
        instance = new FacebookLogger();
        logger = AppEventsLogger.newLogger(context);
    }

    public static FacebookLogger getInstance() {
        return instance;
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logSearchedEvent(String contentType, String searchString, boolean success) {
        if (BuildConfig.DEBUG) {
            return;
        }
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, searchString);
        params.putInt(AppEventsConstants.EVENT_PARAM_SUCCESS, success ? 1 : 0);
        logger.logEvent(AppEventsConstants.EVENT_NAME_SEARCHED, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logViewedContentEvent(String contentType, String contentId, String description, double price) {
        if (BuildConfig.DEBUG) {
            return;
        }
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, description);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CURRENCY_STRING);
        logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, price, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAddedToWishlistEvent(String contentId, String contentType, String description, double price) {
        if (BuildConfig.DEBUG) {
            return;
        }
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, description);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CURRENCY_STRING);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, price, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAddedToCartEvent(String contentId, String contentType, String description, double price) {
        if (BuildConfig.DEBUG) {
            return;
        }
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, description);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CURRENCY_STRING);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, price, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logAddedPaymentInfoEvent(boolean success) {
        if (BuildConfig.DEBUG) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(AppEventsConstants.EVENT_PARAM_SUCCESS, success ? 1 : 0);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ADDED_PAYMENT_INFO, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logInitiatedCheckoutEvent(String contentId, String contentType, String description, int numItems, boolean paymentInfoAvailable, double totalPrice) {
        if (BuildConfig.DEBUG) {
            return;
        }
        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, description);
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        params.putInt(AppEventsConstants.EVENT_PARAM_PAYMENT_INFO_AVAILABLE, paymentInfoAvailable ? 1 : 0);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CURRENCY_STRING);
        logger.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, totalPrice, params);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void logPurchasedEvent(int numItems, String contentType, String contentId, String description, double price) {
        if (BuildConfig.DEBUG) {
            return;
        }
        Bundle params = new Bundle();
        params.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, numItems);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, contentType);
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, contentId);
        params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, description);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, CURRENCY_STRING);
        logger.logPurchase(BigDecimal.valueOf(price), Currency.getInstance(CURRENCY_STRING), params);
    }
}
