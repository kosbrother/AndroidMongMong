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
import com.kosbrother.mongmongwoo.ShoppingCarActivity;

public class PurchaseFragment4 extends Fragment implements View.OnClickListener {

    public static final String ARG_SHIPPING_NAME = "ARG_SHIPPING_NAME";

    private static final String HINT_MESSAGE_LOGIN = "您可以在「我的訂單」掌握商品的最新動向。";
    private static final String HINT_MESSAGE_NOT_LOGIN = "此次的購物明細已寄至您的信箱，煩請確認。";

    private String thankYouMessage =
            "親愛的%s，非常感謝您在萌萌屋消費！" + "\n"
                    + "%s" + "\n"
                    + "萌萌屋祝您平安順心！！";

    public static Fragment newInstance(String shippingName) {
        PurchaseFragment4 fragment = new PurchaseFragment4();

        Bundle args = new Bundle();
        args.putString(ARG_SHIPPING_NAME, shippingName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Settings.checkIsLogIn(getContext())) {
            thankYouMessage = String.format(thankYouMessage,
                    getArguments().getString(ARG_SHIPPING_NAME),
                    HINT_MESSAGE_LOGIN);
        } else {
            thankYouMessage = String.format(thankYouMessage,
                    getArguments().getString(ARG_SHIPPING_NAME),
                    HINT_MESSAGE_NOT_LOGIN);
        }
        // TODO: 2016/4/18 send event by click is better
        ((ShoppingCarActivity) getActivity()).sendShoppoingFragment(4);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchase4, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView thankYouTextView = (TextView) view.findViewById(R.id.thank_you_tv);
        thankYouTextView.setText(thankYouMessage);
        view.findViewById(R.id.finish_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        getActivity().finish();
    }
}
