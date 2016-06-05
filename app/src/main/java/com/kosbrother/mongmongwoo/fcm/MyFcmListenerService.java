package com.kosbrother.mongmongwoo.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
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
import java.util.Map;

public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyFcmListenerService";

    @Override
    public void onMessageReceived(RemoteMessage message) {
//        String from = message.getFrom();
        Map data = message.getData();

        if (orderMessage(data)) {
            onReceivedOrder(data);
        } else {
            onReceivedProduct(data);
        }
    }

    private void onReceivedOrder(Map data) {
        String contentText = (String) data.get("content_text");

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                .setSmallIcon(R.mipmap.ic_mhouse)
                .setContentTitle((String) data.get("content_title"))
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setContentIntent(getPastOrderPendingIntent(data))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        sendNotification(notificationBuilder);
        GAManager.sendEvent(new NotificationPickUpSendEvent((String) data.get("order_id")));
    }

    private void onReceivedProduct(Map data) {
        Bitmap productBitmap = getProductBitmap(data);
        String contentTitle = (String) data.get("content_title");
        String contentText = (String) data.get("content_text");

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

    private Bitmap getProductBitmap(Map data) {
        Bitmap bitmap;
        try {
            URL url = new URL((String) data.get("content_pic"));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            bitmap = BitmapFactory.decodeResource(
                    getApplication().getResources(),
                    R.mipmap.img_pre_load_rectangle);
        }
        return bitmap;
    }

    private Product getProduct(Map data) {
        Product product = new Product(
                Integer.parseInt((String) data.get("item_id")),
                (String) data.get("item_name"),
                Integer.parseInt((String) data.get("item_price")),
                "");
        product.setCategoryName((String) data.get("category_name"));
        // TODO: 2016/6/3 prepare for next version
        String categoryId = (String) data.get("category_id");
        return product;
    }

    private PendingIntent getPastOrderPendingIntent(Map data) {
        Intent pastOrderIntent = new Intent(this, PastOrderDetailActivity.class);
        pastOrderIntent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID,
                Integer.valueOf((String) data.get("order_id")));
        pastOrderIntent.putExtra(PastOrderDetailActivity.EXTRA_BOOLEAN_FROM_NOTIFICATION,
                true);
        return PendingIntent.getActivity(
                this,
                0,
                pastOrderIntent,
                PendingIntent.FLAG_ONE_SHOT
        );
    }

    private PendingIntent getProductPendingIntent(Map data) {
        Intent intent = new Intent(this, ProductActivity.class);
        Product product = getProduct(data);
        intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
        intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, product.getCategoryName());
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

    private boolean orderMessage(Map data) {
        return data.containsKey("order_id");
    }

    private void sendNotification(NotificationCompat.Builder notificationBuilder) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}