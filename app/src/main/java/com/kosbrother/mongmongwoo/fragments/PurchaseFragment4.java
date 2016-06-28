package com.kosbrother.mongmongwoo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep4ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep4EnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.pastorders.PastOrderDetailActivity;

public class PurchaseFragment4 extends Fragment implements View.OnClickListener {

    public static final String ARG_INT_ORDER_ID = "ARG_INT_ORDER_ID";

    private static final String HINT_MESSAGE_LOGIN = "您可以在「我的訂單」掌握商品的最新動向。";
    private static final String HINT_MESSAGE_NOT_LOGIN = "此次購物明細已寄至您的信箱，";

    private String thankYouMessage =
            "親愛的%s，非常感謝您在萌萌屋消費！" + "\n"
                    + "%s" + "\n"
                    + "商品抵達您指定超商時，會有簡訊通知，" + "\n"
                    + "還請您多加留意！萌萌屋全體期待您再次光臨！！";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GAManager.sendEvent(new CheckoutStep4EnterEvent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchase4, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.continue_shopping_btn).setOnClickListener(this);
        view.findViewById(R.id.view_order_btn).setOnClickListener(this);
        setThankYouMessage(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.continue_shopping_btn:
                GAManager.sendEvent(new CheckoutStep4ClickEvent(GALabel.FINISH_PURCHASE));
                getActivity().finish();
                break;
            case R.id.view_order_btn:
                GAManager.sendEvent(new CheckoutStep4ClickEvent(GALabel.VIEW_ORDER));
                Intent intent = new Intent(getActivity(), PastOrderDetailActivity.class);
                intent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID, getOrderId());
                startActivity(intent);
                break;
        }
    }

    private void setThankYouMessage(View view) {
        String shippingName = Settings.getShippingName();
        setThankYouMessage(shippingName);
        Spannable messageSpannable = getMessageSpannable(shippingName);

        TextView thankYouTextView = (TextView) view.findViewById(R.id.thank_you_tv);
        thankYouTextView.setText(messageSpannable);
    }

    private void setThankYouMessage(String shippingName) {
        if (Settings.checkIsLogIn()) {
            thankYouMessage = String.format(thankYouMessage,
                    shippingName,
                    HINT_MESSAGE_LOGIN);
        } else {
            thankYouMessage = String.format(thankYouMessage,
                    shippingName,
                    HINT_MESSAGE_NOT_LOGIN);
        }
    }

    private Spannable getMessageSpannable(String shippingName) {
        Spannable messageSpannable = new SpannableString(thankYouMessage);
        StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(
                ContextCompat.getColor(getContext(), R.color.green_text));

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
        return getArguments().getInt(ARG_INT_ORDER_ID);
    }

}
