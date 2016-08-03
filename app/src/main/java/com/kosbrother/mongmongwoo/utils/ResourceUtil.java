package com.kosbrother.mongmongwoo.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.kosbrother.mongmongwoo.R;

public class ResourceUtil {

    public static int getStatusColorRes(Context context, String status) {
        int colorRes;
        switch (status) {
            case "訂單取消":
            case "未取訂貨":
            case "退貨":
                colorRes = R.color.grey_text_999999;
                break;
            case "訂單變更":
                colorRes = R.color.red_text;
                break;
            default:
                colorRes = R.color.green_text;
                break;
        }
        return ContextCompat.getColor(context, colorRes);
    }
}
