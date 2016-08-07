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
import com.kosbrother.mongmongwoo.utils.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class PastOrdersAdapter extends BaseAdapter {

    private final List<PostOrder> postOrderList;
    private Context context;

    public PastOrdersAdapter(Context context, List<PostOrder> postOrderList) {
        this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_past_order, null);

            viewHolder = new ViewHolder();
            viewHolder.idTextView = (TextView) convertView.findViewById(R.id.item_past_order_id_tv);
            viewHolder.dateTextView = (TextView) convertView.findViewById(R.id.past_order_date_tv);
            viewHolder.totalPriceTextView = (TextView) convertView.findViewById(R.id.past_order_total_price_tv);
            viewHolder.statusTextView = (TextView) convertView.findViewById(R.id.past_order_status_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PostOrder postOrder = postOrderList.get(position);

        viewHolder.idTextView.setText(postOrder.getIdText());
        viewHolder.dateTextView.setText(postOrder.getCreatedOn());
        viewHolder.totalPriceTextView.setText(postOrder.getTotalPriceText());

        TextView statusTextView = viewHolder.statusTextView;
        String status = postOrder.getStatus();
        statusTextView.setText(status);
        statusTextView.setTextColor(ResourceUtil.getStatusColorRes(context, status));

        return convertView;
    }

    public void updateOrders(ArrayList<PostOrder> orders) {
        postOrderList.clear();
        postOrderList.addAll(orders);
    }

    static class ViewHolder {
        TextView idTextView;
        TextView dateTextView;
        TextView totalPriceTextView;
        TextView statusTextView;
    }

}

