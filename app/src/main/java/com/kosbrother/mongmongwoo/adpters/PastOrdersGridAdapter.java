package com.kosbrother.mongmongwoo.adpters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.entity.postorder.PostOrder;

import java.util.ArrayList;
import java.util.List;

public class PastOrdersGridAdapter extends BaseAdapter {

    private final LayoutInflater layoutInflater;
    private final List<PostOrder> postOrderList;

    public PastOrdersGridAdapter(Context context, List<PostOrder> postOrderList) {
        layoutInflater = LayoutInflater.from(context);
        this.postOrderList = postOrderList;
    }

    @Override
    public int getCount() {
        return postOrderList.size();
    }

    @Override
    public PostOrder getItem(int position) {
        return postOrderList.get(position);
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

        PostOrder postOrder = postOrderList.get(position);

        viewHolder.dateTextView.setText(postOrder.getCreatedOn());
        viewHolder.totalPriceTextView.setText(getPrice(postOrder));
        viewHolder.statusTextView.setText(postOrder.getStatus());
        return convertView;
    }

    public void updateOrders(ArrayList<PostOrder> orders) {
        postOrderList.clear();
        postOrderList.addAll(orders);
    }

    private String getPrice(PostOrder postOrder) {
        return "總花費：NT$ " + postOrder.getTotal();
    }

    static class ViewHolder {
        TextView dateTextView;
        TextView totalPriceTextView;
        TextView statusTextView;
    }

}

