package com.kosbrother.mongmongwoo.gcm;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.kosbrother.mongmongwoo.AnalyticsApplication;
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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
        try {
            URL url = new URL(data.getString("content_pic"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            sendNotification(data, bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param data   GCM message received.
     * @param bitmap bitmap for notification big picture style
     */
    private void sendNotification(Bundle data, Bitmap bitmap) {
        Intent intent = new Intent(this, ProductActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Selected_Product", getProduct(data));
        intent.putExtras(bundle);
        intent.putExtra(ProductActivity.BOOLEAN_EXTRA_FROM_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                .setSmallIcon(R.mipmap.ic_mhouse)
                .setContentTitle(data.getString("content_title"))
                .setContentText(data.getString("content_text"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(getBigPictureStyle(data.getString("content_text"), bitmap));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        Tracker mTracker = ((AnalyticsApplication) (getApplication())).getDefaultTracker();
        mTracker.setScreenName("Notification" + data.getString("content_title"));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @NonNull
    private BigPictureStyle getBigPictureStyle(String contentText, Bitmap bitmap) {
        BigPictureStyle style = new BigPictureStyle().bigPicture(bitmap);
        style.setSummaryText(contentText);
        return style;
    }

    @NonNull
    private Product getProduct(Bundle data) {
        return new Product(
                Integer.parseInt(data.getString("item_id")),
                data.getString("item_name"),
                Integer.parseInt(data.getString("item_price")),
                "");
    }
}