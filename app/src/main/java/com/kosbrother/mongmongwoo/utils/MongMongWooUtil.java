package com.kosbrother.mongmongwoo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.kosbrother.mongmongwoo.api.UrlCenter;

public class MongMongWooUtil {

    public static void startToGooglePlayPage(Context context) {
        Uri uri = Uri.parse(UrlCenter.GOOGLE_PLAY_UPDATE);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
}
