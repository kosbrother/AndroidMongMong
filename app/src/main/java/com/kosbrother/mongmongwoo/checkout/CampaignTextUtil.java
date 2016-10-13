package com.kosbrother.mongmongwoo.checkout;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.kosbrother.mongmongwoo.R;

public class CampaignTextUtil {

    public static Spannable getCampaignColorSpannable(Context context, CharSequence title) {
        Spannable messageSpannable = new SpannableString(title);
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(
                ContextCompat.getColor(context, R.color.orange_f5a623));
        ForegroundColorSpan redSpan = new ForegroundColorSpan(
                ContextCompat.getColor(context, R.color.red_text));

        int titleLength = title.length();
        messageSpannable.setSpan(orangeSpan, 0, titleLength,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        messageSpannable.setSpan(new StyleSpan(Typeface.BOLD),
                titleLength - 1, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (title.toString().contains("NT$")) {
            int leftToApplyIndex = title.toString().indexOf("NT$");
            messageSpannable.setSpan(redSpan, leftToApplyIndex, titleLength - 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            messageSpannable.setSpan(new StyleSpan(Typeface.BOLD),
                    0, leftToApplyIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            messageSpannable.setSpan(new StyleSpan(Typeface.BOLD),
                    0, titleLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return messageSpannable;
    }
}
