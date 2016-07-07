package com.kosbrother.mongmongwoo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.login.FacebookUtil;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep2EnterEvent;
import com.kosbrother.mongmongwoo.model.Store;

public class PurchaseFragment2 extends Fragment {

    private OnStep2ButtonClickListener mCallback;

    private Button selectStoreButton;
    private EditText shippingNameEditText;
    private EditText shippingPhoneEditText;
    private EditText shippingEmailEditText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStep2ButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnStep2ButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchase2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectStoreButton = (Button) view.findViewById(R.id.select_store_button);
        shippingNameEditText = (EditText) view.findViewById(R.id.shipping_name_edit_text);
        shippingPhoneEditText = (EditText) view.findViewById(R.id.shipping_phone_edit_text);
        shippingEmailEditText = (EditText) view.findViewById(R.id.shipping_email_edit_text);

        Button nextButton = (Button) view.findViewById(R.id.fragment2_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save to activity order store and shipping name, phone
                if (selectStoreButton.getText().equals(getString(R.string.choose_store))) {
                    Toast.makeText(getActivity(), "請選擇寄件的商店", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isLoginWithValidEmail() && nameOrPhoneEmpty()) {
                    Toast.makeText(getActivity(), "收件人名稱跟電話不可空白", Toast.LENGTH_SHORT).show();
                } else if (!isLoginWithValidEmail() && nameOrPhoneOrEmailEmpty()) {
                    Toast.makeText(getActivity(), "收件人名稱、電話跟Email不可空白", Toast.LENGTH_SHORT).show();
                } else {
                    String shipName = shippingNameEditText.getText().toString().trim();
                    String shipPhone = shippingPhoneEditText.getText().toString().trim();
                    String shipEmail = getUserInputEmailOrFbEmail();
                    mCallback.onStep2NextButtonClick(shipName, shipPhone, shipEmail);
                }
            }
        });
        selectStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSelectStoreClick();
            }
        });

        Store store = Settings.getSavedStore();
        if (store != null) {
            selectStoreButton.setText(store.getName());
            shippingNameEditText.setText(Settings.getShippingName());
            shippingPhoneEditText.setText(Settings.getShippingPhone());
        }
        view.findViewById(R.id.email_ll).setVisibility(isLoginWithValidEmail() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        GAManager.sendEvent(new CheckoutStep2EnterEvent());
    }

    private boolean isLoginWithValidEmail() {
        String email = Settings.getEmail();
        return Settings.checkIsLogIn() && (!email.isEmpty() && !email.contains(FacebookUtil.FAKE_EMAIL_APPEND));
    }

    private boolean nameOrPhoneEmpty() {
        return shippingNameEditText.getText().toString().trim().isEmpty()
                || shippingPhoneEditText.getText().toString().trim().isEmpty();
    }

    private boolean nameOrPhoneOrEmailEmpty() {
        return nameOrPhoneEmpty() || shippingEmailEditText.getText().toString().trim().isEmpty();
    }

    private String getUserInputEmailOrFbEmail() {
        String userInputEmail = shippingEmailEditText.getText().toString().trim();
        if (userInputEmail.isEmpty()) {
            return Settings.getEmail();
        } else {
            return userInputEmail;
        }
    }

    public void updateStoreName(String storeName) {
        selectStoreButton.setText(storeName);
    }

    public interface OnStep2ButtonClickListener {

        void onSelectStoreClick();

        void onStep2NextButtonClick(String shipName, String shipPhone, String shipEmail);
    }
}
