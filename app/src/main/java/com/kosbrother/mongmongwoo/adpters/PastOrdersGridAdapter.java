package com.kosbrother.mongmongwoo.adpters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.PastOrder;

import java.util.ArrayList;

public class PastOrdersGridAdapter extends BaseAdapter {

    private final LayoutInflater layoutInflater;
    private final ArrayList<PastOrder> pastOrderList;

    public PastOrdersGridAdapter(Context context, ArrayList<PastOrder> pastOrderList) {
        layoutInflater = LayoutInflater.from(context);
        this.pastOrderList = pastOrderList;
    }

    @Override
    public int getCount() {
        return pastOrderList.size();
    }

    @Override
    public PastOrder getItem(int position) {
        return pastOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_past_order, null);

            viewHolder = new ViewHolder();
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.past_order_date_tv);
            viewHolder.totalPriceTextView = (TextView) convertView.findViewById(R.id.past_order_total_price_tv);
            viewHolder.statusTextView = (TextView) convertView.findViewById(R.id.past_order_status_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PastOrder pastOrder = pastOrderList.get(position);

        viewHolder.dateTextView.setText(pastOrder.getDate());
        viewHolder.totalPriceTextView.setText(getPrice(pastOrder));
        viewHolder.statusTextView.setText(pastOrder.getStatus());
        return convertView;
    }

    public void updateOrders(ArrayList<PastOrder> orders) {
        pastOrderList.clear();
        pastOrderList.addAll(orders);
    }

    private String getPrice(PastOrder pastOrder) {
        return "總花費：NT$ " + pastOrder.getTotal();
    }

    static class ViewHolder {
        TextView dateTextView;
        TextView totalPriceTextView;
        TextView statusTextView;
    }

}

