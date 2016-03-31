package com.jasonko.mongmongwoo.adpters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jasonko.mongmongwoo.PastOrderDetailActivity;
import com.jasonko.mongmongwoo.R;
import com.jasonko.mongmongwoo.model.PastOrder;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/31/16.
 */
public class PastOrdersGridAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<PastOrder> pastOrders;

    public PastOrdersGridAdapter(Activity activity, ArrayList<PastOrder> pastOrders) {
        mActivity = activity;
        this.pastOrders = pastOrders;
    }

    @Override
    public int getCount() {
        return pastOrders.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // If convertView is null then inflate the appropriate layout file
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_past_order, null);
        }

        TextView textDate = (TextView) convertView.findViewById(R.id.past_order_date_text);
        TextView textTotalPrice = (TextView) convertView.findViewById(R.id.past_order_total_price_text);
        TextView textStatus = (TextView) convertView.findViewById(R.id.past_order_status_text);

        textDate.setText(pastOrders.get(position).getDate());
        textTotalPrice.setText("總花費:NT$" + Integer.toString(pastOrders.get(position).getTotal_price()));
        textStatus.setText(pastOrders.get(position).getStatus());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, PastOrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("THE_ORDER", pastOrders.get(position));
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });

        return convertView;
    }

}

