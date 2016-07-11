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
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep2EnterEvent;
import com.kosbrother.mongmongwoo.login.FacebookUtil;
import com.kosbrother.mongmongwoo.model.Store;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                String shipName = shippingNameEditText.getText().toString().trim();
                String shipPhone = shippingPhoneEditText.getText().toString().trim();
                String shipEmail = getUserInputEmailOrFbEmail();

                String validateResult = validate(shipName, shipPhone, shipEmail);
                if (!validateResult.isEmpty()) {
                    Toast.makeText(getActivity(), validateResult, Toast.LENGTH_SHORT).show();
                    return;
                }

                mCallback.onStep2NextButtonClick(shipName, shipPhone, shipEmail);
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

    private String validate(String shipName, String shipPhone, String shipEmail) {
        if (selectStoreButton.getText().equals(getString(R.string.choose_store))) {
            return "請選擇寄件的商店";
        }
        if (isLoginWithValidEmail() && (shipName.isEmpty() || shipPhone.isEmpty())) {
            return "收件人名稱跟電話不可空白";
        } else if (!isLoginWithValidEmail() && (shipName.isEmpty() || shipPhone.isEmpty() || shipEmail.isEmpty())) {
            return "收件人名稱、電話跟Email不可空白";
        }
        String pattern = "^(\\(?\\+?886\\)?(\\s|-)?9\\d{2}|09\\d{2})(\\s|-)?\\d{3}(\\s|-)?\\d{3}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(shipPhone);
        if (m.matches() && shipPhone.length() == 10) {
            return "";
        } else {
            return "請輸入正確的手機號碼";
        }
    }

    private boolean isLoginWithValidEmail() {
        String email = Settings.getEmail();
        return Settings.checkIsLogIn() && (!email.isEmpty() && !email.contains(FacebookUtil.FAKE_EMAIL_APPEND));
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
