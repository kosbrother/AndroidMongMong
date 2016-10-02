package com.kosbrother.mongmongwoo.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep4ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep4EnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.pastorders.PastOrderDetailActivity;

public class ThankYouActivity extends BaseActivity {

    public static final String EXTRA_INT_ORDER_ID = "EXTRA_INT_ORDER_ID";
    public static final String EXTRA_STRING_SHIP_NAME = "EXTRA_STRING_SHIP_NAME";

    private static final String HINT_MESSAGE_LOGIN = "您可以在「我的訂單」掌握商品的最新動向。";
    private static final String HINT_MESSAGE_NOT_LOGIN = "此次購物明細已寄至您的信箱，";

    private String thankYouMessage =
            "親愛的%s，非常感謝您在萌萌屋消費！" + "\n"
                    + "%s" + "\n"
                    + "商品抵達您指定超商時，會有簡訊通知，" + "\n"
                    + "還請您多加留意！萌萌屋全體期待您再次光臨！！";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        GAManager.sendEvent(new CheckoutStep4EnterEvent());
        setThankYouMessage();
    }

    public void onViewOrderButtonClick(View view) {
        GAManager.sendEvent(new CheckoutStep4ClickEvent(GALabel.VIEW_ORDER));
        Intent intent = new Intent(this, PastOrderDetailActivity.class);
        intent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID, getOrderId());
        startActivity(intent);
    }

    public void onContinueShoppingButtonClick(View view) {
        GAManager.sendEvent(new CheckoutStep4ClickEvent(GALabel.FINISH_PURCHASE));
        finish();
    }

    private void setThankYouMessage() {
        String shippingName = getIntent().getStringExtra(EXTRA_STRING_SHIP_NAME);
        setThankYouMessage(shippingName);
        Spannable messageSpannable = getMessageSpannable(shippingName);

        TextView thankYouTextView = (TextView) findViewById(R.id.activity_thank_you_message_tv);
        thankYouTextView.setText(messageSpannable);
    }

    private void setThankYouMessage(String shipName) {
        if (Settings.checkIsLogIn()) {
            thankYouMessage = String.format(thankYouMessage,
                    shipName,
                    HINT_MESSAGE_LOGIN);
        } else {
            thankYouMessage = String.format(thankYouMessage,
                    shipName,
                    HINT_MESSAGE_NOT_LOGIN);
        }
    }

    private Spannable getMessageSpannable(String shippingName) {
        Spannable messageSpannable = new SpannableString(thankYouMessage);
        StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.green_text));

        int spanStartIndex = thankYouMessage.indexOf(shippingName);
        messageSpannable.setSpan(colorSpan,
                spanStartIndex, spanStartIndex + shippingName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        messageSpannable.setSpan(boldSpan,
                spanStartIndex, spanStartIndex + shippingName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return messageSpannable;
    }

    private int getOrderId() {
        return getIntent().getIntExtra(EXTRA_INT_ORDER_ID, 0);
    }

}
