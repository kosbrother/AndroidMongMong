package com.kosbrother.mongmongwoo.checkout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.SelectDeliverStoreActivity;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.login.FacebookUtil;
import com.kosbrother.mongmongwoo.model.Store;

public class StoreDeliveryFragment extends CheckoutStep2Fragment {

    private static final int REQUEST_SELECT_STORE = 112;

    private TextView selectStoreTextView;
    private EditText shipNameEditText;
    private EditText shipPhoneEditText;
    private EditText shipEmailEditText;

    private Store store = Settings.getSavedStore();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_delivery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectStoreTextView = (TextView) view.findViewById(R.id.store_delivery_select_store_tv);
        shipNameEditText = (EditText) view.findViewById(R.id.store_delivery_ship_name_et);
        shipPhoneEditText = (EditText) view.findViewById(R.id.store_delivery_ship_phone_et);
        shipEmailEditText = (EditText) view.findViewById(R.id.store_delivery_ship_email_et);

        setSelectStoreTextView();
        initUserShipInfoTextView();
        setNextButton(view);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_STORE) {
            if (resultCode == Activity.RESULT_OK) {
                store = (Store) data.getSerializableExtra(SelectDeliverStoreActivity.EXTRA_SELECTED_STORE);
                Settings.saveTempStoreData(store);
                selectStoreTextView.setText(store.getName());
            }
        }
    }

    private void setSelectStoreTextView() {
        selectStoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectStoreIntent = new Intent(getContext(), SelectDeliverStoreActivity.class);
                startActivityForResult(selectStoreIntent, REQUEST_SELECT_STORE);
            }
        });

        selectStoreTextView.setText(store.getName());
    }

    private void initUserShipInfoTextView() {
        shipNameEditText.setText(Settings.getShipName());
        shipPhoneEditText.setText(Settings.getShipPhone());

        String shipEmail = Settings.getShipEmail();
        String loginUserEmail = Settings.getEmail();
        if (loginUserEmail.contains(FacebookUtil.FAKE_EMAIL_APPEND)) {
            loginUserEmail = "";
        }
        String shipEmailText = shipEmail.isEmpty() ? loginUserEmail : shipEmail;
        shipEmailEditText.setText(shipEmailText);
    }

    private void setNextButton(View view) {
        view.findViewById(R.id.fragment2_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectStoreTextView.getText().equals(getString(R.string.choose_store))) {
                    Toast.makeText(getActivity(), "請選擇寄件的商店", Toast.LENGTH_SHORT).show();
                    return;
                }

                String shipName = shipNameEditText.getText().toString().trim().replaceAll(" ", "");
                String shipPhone = shipPhoneEditText.getText().toString().trim().replaceAll(" ", "");
                String shipEmail = shipEmailEditText.getText().toString().trim().replaceAll(" ", "");

                String validateResult = validateUserInfo(shipName, shipPhone, shipEmail);
                if (!validateResult.isEmpty()) {
                    Toast.makeText(getActivity(), validateResult, Toast.LENGTH_SHORT).show();
                    return;
                }

                Settings.saveTempUserShipInfo(shipName, shipPhone, shipEmail);

                hideKeyboard();
                mCallback.onStep2NextButtonClick(store, "", shipName, shipPhone, shipEmail);
            }
        });
    }

}
