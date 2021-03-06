package com.kosbrother.mongmongwoo.launch;

import com.kosbrother.mongmongwoo.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

public class NewAppActivity extends AppCompatActivity {

    public static final String EXTRA_STRING_URL = "EXTRA_STRING_URL";
    public static final String EXTRA_STRING_COUPON = "EXTRA_STRING_COUPON";
    private static final String MESSAGE_WITT_COUPON =
            "為了服務更多的萌友們，萌萌屋要換到新的平台了！舊的APP將不再提供服務，請萌友們至Google Play下載新的APP\n\n下載新的APP後，可以輸入優惠碼: %s\n\n我們會提供舊APP換新APP的專屬優惠哦！！";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_app);

        String coupon = getIntent().getStringExtra(EXTRA_STRING_COUPON);
        Spannable messageSpannable = getMessageSpannable(coupon);
        TextView messageTextView = (TextView) findViewById(R.id.activity_new_app_message_tv);
        messageTextView.setText(messageSpannable);
    }

    public void onGoToNewMmwClick(View view) {
        String urlString = getIntent().getStringExtra(EXTRA_STRING_URL);
        Uri uri = Uri.parse(urlString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private Spannable getMessageSpannable(String coupon) {
        String newAppMessage = String.format(MESSAGE_WITT_COUPON, coupon);
        Spannable messageSpannable = new SpannableString(newAppMessage);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.red_text));

        int start = newAppMessage.indexOf(coupon);
        int end = start + coupon.length();
        messageSpannable.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return messageSpannable;
    }
}
