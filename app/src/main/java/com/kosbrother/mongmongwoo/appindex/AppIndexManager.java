package com.kosbrother.mongmongwoo.appindex;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kosbrother.mongmongwoo.model.Product;

public class AppIndexManager {

    public static final String MAIN_URI_STRING = "https://www.mmwooo.com/";
    public static final String ITEM_URI_STRING =
            "https://www.mmwooo.com/categories/%s/items/%s";

    private static GoogleApiClient mClient = null;

    public static void init(Context context) {
        if (mClient == null) {
            mClient = new GoogleApiClient.Builder(context).addApi(AppIndex.API).build();
        }
    }

    public static void startMainAppIndex() {
        if (mClient != null) {
            mClient.connect();
            AppIndex.AppIndexApi.start(mClient, getMainAction());
        }
    }

    public static void stopMainAppIndex() {
        if (mClient != null) {
            AppIndex.AppIndexApi.end(mClient, getMainAction());
            mClient.disconnect();
            mClient = null;
        }
    }

    public static void startItemAppIndex(Product theProduct) {
        if (mClient != null) {
            mClient.connect();
            AppIndex.AppIndexApi.start(mClient, getItemAction(theProduct));
        }
    }

    public static void stopItemAppIndex(Product product) {
        if (mClient != null) {
            AppIndex.AppIndexApi.end(mClient, getItemAction(product));
            mClient.disconnect();
            mClient = null;
        }
    }

    private static Action getMainAction() {
        Thing object = new Thing.Builder()
                .setName("萌萌屋")
                .setDescription("走在青年流行前線")
                .setUrl(Uri.parse(MAIN_URI_STRING))
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    private static Action getItemAction(Product theProduct) {
        String urlString = String.format(ITEM_URI_STRING,
                theProduct.getCategoryName(), theProduct.getName());
        Thing object = new Thing.Builder()
                .setName(theProduct.getName())
                .setDescription(theProduct.getDescription())
                .setUrl(Uri.parse(urlString))
                .build();

        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
