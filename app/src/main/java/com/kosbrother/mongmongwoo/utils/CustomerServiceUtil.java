package com.kosbrother.mongmongwoo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.kosbrother.mongmongwoo.api.UrlCenter;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.customerservice.CustomerServiceClickEvent;

public class CustomerServiceUtil {

    public static void startToLineService(Context context) {
        GAManager.sendEvent(new CustomerServiceClickEvent("經由LINE傳送"));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(UrlCenter.CUSTOMER_SERVICE_LINE));
        context.startActivity(intent);
    }

    public static void startToFbService(Context context) {
        GAManager.sendEvent(new CustomerServiceClickEvent("經由粉絲頁傳送"));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(UrlCenter.CUSTOMER_SERVICE_FB));
        context.startActivity(intent);
    }
}
