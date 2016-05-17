package com.kosbrother.mongmongwoo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep4ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;

public class PurchaseFragment4 extends Fragment implements View.OnClickListener {

    private static final String HINT_MESSAGE_LOGIN = "您可以在「我的訂單」掌握商品的最新動向。";
    private static final String HINT_MESSAGE_NOT_LOGIN = "此次購物明細已寄至您的信箱，";

    private String thankYouMessage =
            "親愛的%s，非常感謝您在萌萌屋消費！" + "\n"
                    + "%s" + "\n"
                    + "商品抵達您指定超商時，會有簡訊通知，" + "\n"
                    + "還請您多加留意！萌萌屋全體期待您再次光臨！！";

    public static Fragment newInstance() {
        return new PurchaseFragment4();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchase4, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.finish_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        GAManager.sendEvent(new CheckoutStep4ClickEvent(GALabel.FINISH_PURCHASE));
        getActivity().finish();
    }

    public void setThankYouMessage() {
        if (Settings.checkIsLogIn()) {
            thankYouMessage = String.format(thankYouMessage,
                    Settings.getShippingName(),
                    HINT_MESSAGE_LOGIN);
        } else {
            thankYouMessage = String.format(thankYouMessage,
                    Settings.getShippingName(),
                    HINT_MESSAGE_NOT_LOGIN);
        }
        assert getView() != null;
        TextView thankYouTextView = (TextView) getView().findViewById(R.id.thank_you_tv);
        thankYouTextView.setText(thankYouMessage);
    }

}
