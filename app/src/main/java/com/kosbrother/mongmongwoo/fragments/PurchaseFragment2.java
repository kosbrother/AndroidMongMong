package com.kosbrother.mongmongwoo.fragments;

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
import com.kosbrother.mongmongwoo.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.model.Store;

/**
 * Created by kolichung on 3/9/16.
 */
public class PurchaseFragment2 extends Fragment {

    Button nextButton;
    Button selectStoreButton;
    EditText shippingNameEditText;
    EditText shippingPhoneEditText;
    EditText shippingEmailEditText;

    public static PurchaseFragment2 newInstance() {
        PurchaseFragment2 fragment = new PurchaseFragment2();
        return fragment;
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

        view.findViewById(R.id.email_ll).setVisibility(isLogin() ? View.GONE : View.VISIBLE);
        shippingEmailEditText = (EditText) view.findViewById(R.id.shipping_email_edit_text);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save to activity order store and shipping name, phone
                ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
                if (activity.getOrder().getShippingStore() == null) {
                    Toast.makeText(getActivity(), "請選擇寄件的商店", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (isLogin() && nameOrPhoneEmpty()) {
                    Toast.makeText(getActivity(), "收件人名稱跟電話不可空白", Toast.LENGTH_SHORT).show();
                } else if (!isLogin() && nameOrPhoneOrEmailEmpty()) {
                    Toast.makeText(getActivity(), "收件人名稱、電話跟Email不可空白", Toast.LENGTH_SHORT).show();
                } else {
                    activity.getOrder().setShippingName(shippingNameEditText.getText().toString());
                    activity.getOrder().setShippingPhone(shippingPhoneEditText.getText().toString());
                    activity.getOrder().setShippingEmail(shippingEmailEditText.getText().toString());
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

        ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
        if (activity.getOrder().getShippingStore() != null) {
            selectStoreButton.setText(activity.getOrder().getShippingStore().getName());
            shippingNameEditText.setText(activity.getOrder().getShippingName());
            shippingPhoneEditText.setText(activity.getOrder().getShippingPhone());
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            Bundle bundle = data.getExtras();
            Store theStore = (Store) bundle.getSerializable("Selected_Store");
            ShoppingCarActivity mActivity = (ShoppingCarActivity) getActivity();
            mActivity.setSelectedStore(theStore);
            selectStoreButton.setText(theStore.getName());
        }
    }

    public void setEmailLayoutByLoginStatus() {
        getView().findViewById(R.id.email_ll).setVisibility(isLogin() ? View.GONE : View.VISIBLE);
    }

    private boolean isLogin() {
        return Settings.checkIsLogIn();
    }

    private boolean nameOrPhoneEmpty() {
        return shippingNameEditText.getText().toString().isEmpty()
                || shippingPhoneEditText.getText().toString().isEmpty();
    }

    private boolean nameOrPhoneOrEmailEmpty() {
        return nameOrPhoneEmpty() || shippingEmailEditText.getText().toString().isEmpty();
    }
}
