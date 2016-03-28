package com.jasonko.mongmongwoo.adpters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonko.mongmongwoo.PastOrderDetailActivity;
import com.jasonko.mongmongwoo.R;
import com.jasonko.mongmongwoo.model.PastOrder;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/28/16.
 */
public class PastOrdersAdapter extends RecyclerView.Adapter<PastOrdersAdapter.ViewHolder> {

    public ArrayList<PastOrder> pastOrders;
    public Activity mActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView textDate;
        public TextView textTotalPrice;
        public TextView textStatus;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            textDate = (TextView) mView.findViewById(R.id.past_order_date_text);
            textTotalPrice = (TextView) mView.findViewById(R.id.past_order_total_price_text);
            textStatus = (TextView) mView.findViewById(R.id.past_order_status_text);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PastOrdersAdapter(Activity activity, ArrayList<PastOrder> pastOrders) {
        this.pastOrders = pastOrders;
        this.mActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PastOrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_past_order, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textDate.setText(pastOrders.get(position).getDate());
        holder.textTotalPrice.setText("總花費:NT$" + Integer.toString(pastOrders.get(position).getTotal_price()));
        holder.textStatus.setText(pastOrders.get(position).getStatus());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, PastOrderDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("THE_ORDER", pastOrders.get(position));
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return pastOrders.size();
    }

}