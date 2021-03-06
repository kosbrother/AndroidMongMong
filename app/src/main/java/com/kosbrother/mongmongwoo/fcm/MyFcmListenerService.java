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

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kosbrother.mongmongwoo.BuildConfig;
import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationMyMessageSendEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPickUpSendEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPromoSendEvent;
import com.kosbrother.mongmongwoo.launch.NewAppActivity;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.mynotification.MyNotificationListActivity;
import com.kosbrother.mongmongwoo.pastorders.PastOrderDetailActivity;
import com.kosbrother.mongmongwoo.product.ProductActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MyFcmListenerService extends FirebaseMessagingService {

    private static final String KEY_CONTENT_TEXT = "content_text";
    private static final String KEY_CONTENT_TITLE = "content_title";

    private int id;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        id = (int) System.currentTimeMillis();
        Map data = message.getData();

        if (data.containsKey("coupon")) {
            onReceivedNewAppNotification(data);
        }else if (data.containsKey("order_id")) {
            onReceivedOrder(data);
        } else if (data.containsKey("m_type")) {
            onReceivedMyNotification(data);
        } else if (data.containsKey("content_pic")) {
            onReceivedProduct(data);
        }
    }

    private void onReceivedNewAppNotification(Map data) {
        String contentText = data.get(KEY_CONTENT_TEXT).toString();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                .setSmallIcon(R.mipmap.ic_mhouse)
                .setContentTitle(data.get(KEY_CONTENT_TITLE).toString())
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setContentIntent(getNewAppPendingIntent(data))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        sendNotification(notificationBuilder);
    }

    private void onReceivedOrder(Map data) {
        String contentText = data.get(KEY_CONTENT_TEXT).toString();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                .setSmallIcon(R.mipmap.ic_mhouse)
                .setContentTitle(data.get(KEY_CONTENT_TITLE).toString())
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setContentIntent(getPastOrderPendingIntent(data))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        sendNotification(notificationBuilder);
        GAManager.sendEvent(new NotificationPickUpSendEvent(data.get("order_id").toString()));
    }

    private void onReceivedMyNotification(Map data) {
        String contentTitle = data.get(KEY_CONTENT_TITLE).toString();
        String contentText = data.get(KEY_CONTENT_TEXT).toString();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                .setSmallIcon(R.mipmap.ic_mhouse)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setContentIntent(getMyNotificationPendingIntent(data))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        sendNotification(notificationBuilder);
        GAManager.sendEvent(new NotificationMyMessageSendEvent(contentTitle));
    }

    private void onReceivedProduct(Map data) {
        getProductBitmap(data);
    }

    private void getProductBitmap(final Map data) {
        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                InputStream in = null;
                try {
                    URL url = new URL(data.get("content_pic").toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    in = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    subscriber.onNext(bitmap);
                } catch (IOException e) {
                    if (!BuildConfig.DEBUG) {
                        Crashlytics.log("Get Notification bitmap exception.");
                        Crashlytics.logException(e);
                    }
                    Bitmap bitmap = BitmapFactory.decodeResource(
                            getApplication().getResources(),
                            R.mipmap.img_pre_load_rectangle);
                    subscriber.onNext(bitmap);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    subscriber.onCompleted();
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        String contentTitle = data.get(KEY_CONTENT_TITLE).toString();
                        String contentText = data.get(KEY_CONTENT_TEXT).toString();

                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MyFcmListenerService.this)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon9))
                                .setSmallIcon(R.mipmap.ic_mhouse)
                                .setContentTitle(contentTitle)
                                .setContentText(contentText)
                                .setAutoCancel(true)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setContentIntent(getProductPendingIntent(data))
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setStyle(getBigPictureStyle(contentText, bitmap));

                        sendNotification(notificationBuilder);
                        GAManager.sendEvent(new NotificationPromoSendEvent(contentTitle));
                    }
                });
    }

    private Product getProduct(Map data) {
        Product product = new Product(
                Integer.parseInt(data.get("item_id").toString()),
                data.get("item_name").toString(),
                Integer.parseInt(data.get("item_price").toString()),
                "");
        product.setCategoryName(data.get("category_name").toString());
        product.setCategoryId(Integer.parseInt(data.get("category_id").toString()));
        return product;
    }

    private PendingIntent getNewAppPendingIntent(Map data) {
        Intent pastOrderIntent = new Intent(this, NewAppActivity.class);
        pastOrderIntent.putExtra(NewAppActivity.EXTRA_STRING_COUPON,
                data.get("coupon").toString());
        pastOrderIntent.putExtra(NewAppActivity.EXTRA_STRING_URL,
                data.get("url").toString());
        return PendingIntent.getActivity(
                this,
                id /* Request code */,
                pastOrderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private PendingIntent getPastOrderPendingIntent(Map data) {
        Intent pastOrderIntent = new Intent(this, PastOrderDetailActivity.class);
        pastOrderIntent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID,
                Integer.parseInt(data.get("order_id").toString()));
        pastOrderIntent.putExtra(PastOrderDetailActivity.EXTRA_BOOLEAN_FROM_NOTIFICATION,
                true);
        return PendingIntent.getActivity(
                this,
                id /* Request code */,
                pastOrderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private PendingIntent getMyNotificationPendingIntent(Map data) {
        Intent intent;
        if (Settings.checkIsLogIn()) {
            intent = new Intent(this, MyNotificationListActivity.class);
            intent.putExtra(MyNotificationListActivity.EXTRA_BOOLEAN_FROM_NOTIFICATION, true);
            intent.putExtra(MyNotificationListActivity.EXTRA_STRING_NOTIFICATION_TITLE, data.get(KEY_CONTENT_TITLE).toString());
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_BOOLEAN_FROM_NOTIFICATION, true);
            intent.putExtra(MainActivity.EXTRA_STRING_NOTIFICATION_TITLE, data.get(KEY_CONTENT_TITLE).toString());
        }
        return PendingIntent.getActivity(
                this,
                id /* Request code */,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getProductPendingIntent(Map data) {
        Intent intent = new Intent(this, ProductActivity.class);
        Product product = getProduct(data);
        intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
        intent.putExtra(ProductActivity.EXTRA_INT_CATEGORY_ID, product.getCategoryId());
        intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, product.getCategoryName());
        intent.putExtra(ProductActivity.EXTRA_BOOLEAN_FROM_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(
                this,
                id /* Request code */,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private BigPictureStyle getBigPictureStyle(String contentText, Bitmap bitmap) {
        BigPictureStyle style = new BigPictureStyle().bigPicture(bitmap);
        style.setSummaryText(contentText);
        return style;
    }

    private void sendNotification(NotificationCompat.Builder notificationBuilder) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
    }
}