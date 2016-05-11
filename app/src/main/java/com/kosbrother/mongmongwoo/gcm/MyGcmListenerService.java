package com.kosbrother.mongmongwoo.gcm;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.kosbrother.mongmongwoo.AnalyticsApplication;
import com.kosbrother.mongmongwoo.PastOrderDetailActivity;
import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.Product;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyGcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, final Bundle data) {
        if (orderMessage(data)) {
            onReceivedOrder(data);
        } else {
            onReceivedProduct(data);
        }
    }
    // [END receive_message]

    private void onReceivedOrder(Bundle data) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                .setSmallIcon(R.mipmap.ic_mhouse)
                .setContentTitle(data.getString("content_title"))
                .setContentText(data.getString("content_text"))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(data.getString("content_text")))
                .setContentIntent(getPastOrderPendingIntent(data))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        sendNotification(notificationBuilder);
    }

    private void onReceivedProduct(Bundle data) {
        Bitmap productBitmap = getProductBitmap(data);
        if (productBitmap != null) {
            sendProductNotification(data, productBitmap);

            Tracker mTracker = ((AnalyticsApplication) (getApplication())).getDefaultTracker();
            mTracker.setScreenName("Notification" + data.getString("content_title"));
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    private void sendProductNotification(Bundle data, Bitmap bitmap) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                .setSmallIcon(R.mipmap.ic_mhouse)
                .setContentTitle(data.getString("content_title"))
                .setContentText(data.getString("content_text"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(getProductPendingIntent(data))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(getBigPictureStyle(data.getString("content_text"), bitmap));

        sendNotification(notificationBuilder);
    }

    private Bitmap getProductBitmap(Bundle data) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(data.getString("content_pic"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Product getProduct(Bundle data) {
        return new Product(
                Integer.parseInt(data.getString("item_id")),
                data.getString("item_name"),
                Integer.parseInt(data.getString("item_price")),
                "");
    }

    private PendingIntent getPastOrderPendingIntent(Bundle data) {
        Intent pastOrderIntent = new Intent(this, PastOrderDetailActivity.class);
        pastOrderIntent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID,
                Integer.valueOf(data.getString("order_id")));
        return PendingIntent.getActivity(
                this,
                0,
                pastOrderIntent,
                PendingIntent.FLAG_ONE_SHOT
        );
    }

    private PendingIntent getProductPendingIntent(Bundle data) {
        Intent intent = new Intent(this, ProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Selected_Product", getProduct(data));
        intent.putExtras(bundle);
        intent.putExtra(ProductActivity.BOOLEAN_EXTRA_FROM_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
    }

    private BigPictureStyle getBigPictureStyle(String contentText, Bitmap bitmap) {
        BigPictureStyle style = new BigPictureStyle().bigPicture(bitmap);
        style.setSummaryText(contentText);
        return style;
    }

    private boolean orderMessage(Bundle data) {
        return data.containsKey("order_id");
    }

    private void sendNotification(NotificationCompat.Builder notificationBuilder) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}