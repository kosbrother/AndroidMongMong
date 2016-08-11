package com.kosbrother.mongmongwoo.utils;

import android.graphics.Paint;
import android.widget.TextView;

public class TextViewUtil {
    public static void paintLineThroughTextView(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public static void removeLineThroughTextView(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }
}
