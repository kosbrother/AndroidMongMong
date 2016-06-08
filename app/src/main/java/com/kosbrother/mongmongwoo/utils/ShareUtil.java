package com.kosbrother.mongmongwoo.utils;

import android.content.Context;
import android.content.Intent;

public class ShareUtil {

    public static void shareText(Context context, String title, String subject, String text) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(i, title));
    }
}
