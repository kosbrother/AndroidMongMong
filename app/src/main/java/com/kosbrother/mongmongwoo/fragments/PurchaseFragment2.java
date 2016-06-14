package com.kosbrother.mongmongwoo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.SelectDeliverStoreActivity;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.facebook.FacebookUtil;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCarActivity;

public class PurchaseFragment2 extends Fragment {

    Button nextButton;
    Button selectStoreButton;
    EditText shippingNameEditText;
    EditText shippingPhoneEditText;
    EditText shippingEmailEditText;

    public static PurchaseFragment2 newInstance() {
        return new PurchaseFragment2();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_purchase2, container, false);
        nextButton = (Button) view.findViewById(R.id.fragment2_next_button);
        selectStoreButton = (Button) view.findViewById(R.id.select_store_button);
        shippingNameEditText = (EditText) view.findViewById(R.id.shipping_name_edit_text);
        shippingPhoneEditText = (EditText) view.findViewById(R.id.shipping_phone_edit_text);
        shippingEmailEditText = (EditText) view.findViewById(R.id.shipping_email_edit_text);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save to activity order store and shipping name, phone
                ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
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

                    activity.saveShippingInfo(shipName, shipPhone, shipEmail);
                    Settings.saveUserShippingNameAndPhone(shipName, shipPhone);

                    activity.startPurchaseFragment3();

                    View view = activity.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        selectStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectStoreIntent = new Intent(getActivity(), SelectDeliverStoreActivity.class);
                startActivityForResult(selectStoreIntent, 200);
            }
        });

        Store store = Settings.getSavedStore();
        if (store != null) {
            selectStoreButton.setText(store.getName());
            shippingNameEditText.setText(Settings.getShippingName());
            shippingPhoneEditText.setText(Settings.getShippingPhone());
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            Store theStore = (Store) bundle.getSerializable("Selected_Store");
            ShoppingCarActivity mActivity = (ShoppingCarActivity) getActivity();
            mActivity.saveStoreInfo(theStore);
            Settings.saveUserStoreData(theStore);
            selectStoreButton.setText(theStore.getName());
        }
    }

    public void setEmailLayoutByLoginStatus() {
        getView().findViewById(R.id.email_ll).setVisibility(isLoginWithValidEmail() ? View.GONE : View.VISIBLE);
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
}
