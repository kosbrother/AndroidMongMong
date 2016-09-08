package com.kosbrother.mongmongwoo.checkout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.login.BaseNoTitleDialog;

import rx.functions.Action1;

public class DeliveryDialog extends BaseNoTitleDialog implements View.OnClickListener {

    private TextView storeTextView;
    private TextView homeTextView;
    private String selectedDelivery;
    private Action1<String> confirmDeliveryAction;

    public DeliveryDialog(Context context, String selectedDelivery, final Action1<String> confirmDeliveryAction) {
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

        setSelectedTextView(storeTextView);
        setSelectedTextView(homeTextView);

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
        confirmDeliveryAction = null;
        dismiss();
    }
}
