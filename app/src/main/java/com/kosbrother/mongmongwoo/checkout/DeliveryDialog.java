package com.kosbrother.mongmongwoo.checkout;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;

import rx.functions.Action1;

public class DeliveryDialog implements View.OnClickListener {

    private TextView storeTextView;
    private TextView homeTextView;
    private AlertDialog alertDialog;
    private String selectedDelivery;

    public DeliveryDialog(Context context, String selectedDelivery, final Action1<String> confirmDeliveryAction) {
        this.selectedDelivery = selectedDelivery;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delivery, null);
        builder.setView(view)
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmDeliveryAction.call(DeliveryDialog.this.selectedDelivery);

                        storeTextView = null;
                        homeTextView = null;
                        alertDialog = null;
                    }
                })
                .setNegativeButton("取消", null);
        alertDialog = builder.create();

        storeTextView = (TextView) view.findViewById(R.id.dialog_delivery_store_tv);
        storeTextView.setOnClickListener(this);

        homeTextView = (TextView) view.findViewById(R.id.dialog_delivery_home_tv);
        homeTextView.setOnClickListener(this);

        setSelectedTextView(storeTextView);
        setSelectedTextView(homeTextView);
    }

    private void setSelectedTextView(TextView textView) {
        if (selectedDelivery.equals(textView.getText().toString())) {
            textView.setSelected(true);
        }
    }

    public void show() {
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        selectedDelivery = ((TextView) view).getText().toString();
        int id = view.getId();
        switch (id) {
            case R.id.dialog_delivery_store_tv:
                view.setSelected(true);
                homeTextView.setSelected(false);
                break;
            case R.id.dialog_delivery_home_tv:
                view.setSelected(true);
                storeTextView.setSelected(false);
                break;
            default:
                break;
        }
    }
}
