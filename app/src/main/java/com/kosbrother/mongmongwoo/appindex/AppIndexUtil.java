package com.kosbrother.mongmongwoo.appindex;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kosbrother.mongmongwoo.model.Product;

public class AppIndexUtil {

    private static final String MAIN_URL_STRING = "https://www.mmwooo.com";
    private static final String PRODUCT = "https://www.mmwooo.com/categories/%s/items/%s";
    private static final String CATEGORY_URL_STRING = "https://www.mmwooo.com/categories/%s";

    private static final String MAIN_ACTION_NAME = "萌萌屋";
    private static final String MAIN_ACTION_DESCRIPTION = "校園生活補給站";

    public static GoogleApiClient buildAppIndexClient(Context context) {
        return new GoogleApiClient.Builder(context).addApi(AppIndex.API).build();
    }

    public static void startMainAppIndex(GoogleApiClient googleApiClient) {
        startAppIndex(googleApiClient, getMainAction());
    }

    public static void stopMainAppIndex(GoogleApiClient googleApiClient) {
        endAppIndex(googleApiClient, getMainAction());
    }

    public static void startProductAppIndex(GoogleApiClient googleApiClient, Product product) {
        startAppIndex(googleApiClient, getProductAction(product));
    }

    public static void stopProductAppIndex(GoogleApiClient googleApiClient, Product product) {
        endAppIndex(googleApiClient, getProductAction(product));
    }

    public static void startCategoryAppIndex(GoogleApiClient googleApiClient, String categoryName) {
        startAppIndex(googleApiClient, getCategoryAction(categoryName));
    }

    public static void stopCategoryAppIndex(GoogleApiClient googleApiClient, String categoryName) {
        endAppIndex(googleApiClient, getCategoryAction(categoryName));
    }

    private static Action getMainAction() {
        Thing object = new Thing.Builder()
                .setName(MAIN_ACTION_NAME)
                .setDescription(MAIN_ACTION_DESCRIPTION)
                .setUrl(Uri.parse(MAIN_URL_STRING))
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private static Action getProductAction(Product product) {
        String urlString = String.format(PRODUCT,
                product.getCategoryName(), product.getSlug());
        Thing object = new Thing.Builder()
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setUrl(Uri.parse(urlString))
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private static Action getCategoryAction(String categoryName) {
        String urlString = String.format(CATEGORY_URL_STRING, categoryName);
        Thing object = new Thing.Builder()
                .setName(categoryName)
                .setDescription(categoryName)
                .setUrl(Uri.parse(urlString))
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private static void startAppIndex(GoogleApiClient googleApiClient, Action action) {
        if (googleApiClient != null) {
            googleApiClient.connect();
            AppIndex.AppIndexApi.start(googleApiClient, action);
        }
    }

    private static void endAppIndex(GoogleApiClient googleApiClient, Action action) {
        if (googleApiClient != null) {
            AppIndex.AppIndexApi.end(googleApiClient, action);
            googleApiClient.disconnect();
        }
    }
}
