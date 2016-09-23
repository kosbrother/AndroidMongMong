package com.kosbrother.mongmongwoo.checkout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.login.BaseNoTitleDialog;

import rx.functions.Action1;

class DeliveryDialog extends BaseNoTitleDialog implements View.OnClickListener {

    private TextView storeTextView;
    private TextView homeTextView;
    private TextView homeByCreditCardTextView;
    private String selectedDelivery;
    private Action1<String> confirmDeliveryAction;

    DeliveryDialog(Context context, String selectedDelivery, final Action1<String> confirmDeliveryAction) {
        super(context);
        this.selectedDelivery = selectedDelivery;
        this.confirmDeliveryAction = confirmDeliveryAction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delivery);

        storeTextView = (TextView) findViewById(R.id.dialog_delivery_store_tv);
        storeTextView.setOnClickListener(this);

        homeTextView = (TextView) findViewById(R.id.dialog_delivery_home_tv);
        homeTextView.setOnClickListener(this);

        homeByCreditCardTextView = (TextView) findViewById(R.id.dialog_delivery_home_by_credit_card_tv);
        homeByCreditCardTextView.setOnClickListener(this);

        setSelectedTextView(storeTextView);
        setSelectedTextView(homeTextView);
        setSelectedTextView(homeByCreditCardTextView);

        fixDialogSize();
    }

    private void setSelectedTextView(TextView textView) {
        if (selectedDelivery.equals(textView.getText().toString())) {
            textView.setSelected(true);
        }
    }

    @Override
    public void onClick(View view) {
        selectedDelivery = ((TextView) view).getText().toString();
        confirmDeliveryAction.call(selectedDelivery);

        storeTextView = null;
        homeTextView = null;
        homeByCreditCardTextView = null;
        confirmDeliveryAction = null;
        dismiss();
    }
}
