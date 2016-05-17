package com.kosbrother.mongmongwoo.gcm;

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

import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPickUpSendEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPromoSendEvent;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.pastorders.PastOrderDetailActivity;

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
        String contentText = data.getString("content_text");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                .setSmallIcon(R.mipmap.ic_mhouse)
                .setContentTitle(data.getString("content_title"))
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setContentIntent(getPastOrderPendingIntent(data))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        sendNotification(notificationBuilder);
        GAManager.sendEvent(new NotificationPickUpSendEvent(data.getString("order_id")));
    }

    private void onReceivedProduct(Bundle data) {
        Bitmap productBitmap = getProductBitmap(data);
        if (productBitmap != null) {
            String contentTitle = data.getString("content_title");
            String contentText = data.getString("content_text");

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                    .setSmallIcon(R.mipmap.ic_mhouse)
                    .setContentTitle(contentTitle)
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(getProductPendingIntent(data))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setStyle(getBigPictureStyle(contentText, productBitmap));

            sendNotification(notificationBuilder);
            GAManager.sendEvent(new NotificationPromoSendEvent(contentTitle));
        }
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
        pastOrderIntent.putExtra(PastOrderDetailActivity.EXTRA_BOOLEAN_FROM_NOTIFICATION,
                true);
        return PendingIntent.getActivity(
                this,
                0,
                pastOrderIntent,
                PendingIntent.FLAG_ONE_SHOT
        );
    }

    private PendingIntent getProductPendingIntent(Bundle data) {
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, getProduct(data).getId());
        intent.putExtra(ProductActivity.EXTRA_BOOLEAN_FROM_NOTIFICATION, true);
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