package com.kosbrother.mongmongwoo.checkout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.StoreApi;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.login.FacebookUtil;
import com.kosbrother.mongmongwoo.model.County;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.model.Town;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;

import java.util.List;

import rx.functions.Action1;

public class HomeDeliveryFragment extends CheckoutStep2Fragment {

    private List<County> counties = StoreApi.getCounties();
    private List<Town> townArray;
    private Spinner townSpinners;

    private String selectedTown;
    private String selectedCountry;

    private EditText addressEditText;
    private EditText nameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_delivery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addressEditText = (EditText) view.findViewById(R.id.fragment_home_delivery_address_et);
        nameEditText = (EditText) view.findViewById(R.id.fragment_home_delivery_name_et);
        phoneEditText = (EditText) view.findViewById(R.id.fragment_home_delivery_phone_et);
        emailEditText = (EditText) view.findViewById(R.id.fragment_home_delivery_email_et);

        setCountySpinner(view);
        setTownSpinner(view);
        initUserShipInfoTextView();
        setNextButton(view);
    }

    private void setCountySpinner(View view) {
        Spinner countySpinners = (Spinner) view.findViewById(R.id.fragment_home_delivery_country_spinner);
        ArrayAdapter<County> countyArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_drop_down_arrow, counties);
        countyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countySpinners.setAdapter(countyArrayAdapter);

        countySpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = counties.get(position).toString();

                Webservice.getTowns(counties.get(position).getId(), new Action1<ResponseEntity<List<Town>>>() {
                    @Override
                    public void call(ResponseEntity<List<Town>> listResponseEntity) {
                        List<Town> data = listResponseEntity.getData();
                        if (data == null) {
                            GAManager.sendError("getTownsError", listResponseEntity.getError());
                            if (NetworkUtil.getConnectivityStatus(getContext()) == 0) {
                                Toast.makeText(getContext(), "無網路連線", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            townArray = data;
                            ArrayAdapter<Town> townArrayAdapter = new ArrayAdapter<>(
                                    getContext(), R.layout.spinner_drop_down_arrow, townArray);
                            townArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            townSpinners.setAdapter(townArrayAdapter);
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setTownSpinner(View view) {
        townSpinners = (Spinner) view.findViewById(R.id.fragment_home_delivery_town_spinner);
        townSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (townArray != null) {
                    selectedTown = townArray.get(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initUserShipInfoTextView() {
        addressEditText.setText(Settings.getShippingAddress());
        nameEditText.setText(Settings.getShipName());
        phoneEditText.setText(Settings.getShipPhone());

        String shipEmail = Settings.getShipEmail();
        String loginUserEmail = Settings.getEmail();
        if (loginUserEmail.contains(FacebookUtil.FAKE_EMAIL_APPEND)) {
            loginUserEmail = "";
        }
        String shipEmailText = shipEmail.isEmpty() ? loginUserEmail : shipEmail;
        emailEditText.setText(shipEmailText);
    }

    private void setNextButton(View view) {
        view.findViewById(R.id.fragment_home_delivery_next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String detailAddress = addressEditText.getText().toString().trim().replaceAll(" ", "");
                if (detailAddress.isEmpty()) {
                    Toast.makeText(getActivity(), "請輸入詳細地址", Toast.LENGTH_SHORT).show();
                    return;
                }

                String shipName = nameEditText.getText().toString().trim().replaceAll(" ", "");
                String shipPhone = phoneEditText.getText().toString().trim().replaceAll(" ", "");
                String shipEmail = emailEditText.getText().toString().trim().replaceAll(" ", "");

                String validateResult = validateUserInfo(shipName, shipPhone, shipEmail);
                if (!validateResult.isEmpty()) {
                    Toast.makeText(getContext(), validateResult, Toast.LENGTH_SHORT).show();
                    return;
                }

                hideKeyboard();

                Settings.saveTempUserShipDetailAddress(detailAddress);
                Settings.saveTempUserShipInfo(shipName, shipPhone, shipEmail);

                String shipAddress = selectedCountry + selectedTown + detailAddress;
                mCallback.onStep2NextButtonClick(new Store(0, "", "", "", ""), shipAddress, shipName, shipPhone, shipEmail);
            }
        });
    }
}
