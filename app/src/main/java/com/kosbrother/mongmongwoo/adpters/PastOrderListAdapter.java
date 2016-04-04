package com.kosbrother.mongmongwoo.adpters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.PastOrderProduct;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/28/16.
 */
public class PastOrderListAdapter extends RecyclerView.Adapter<PastOrderListAdapter.ViewHolder> {

    public ArrayList<PastOrderProduct> pastOrderProducts;
    public Activity mActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView textName;
        public TextView textPrice;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            textName = (TextView) mView.findViewById(R.id.item_car_name);
            textPrice = (TextView) mView.findViewById(R.id.item_car_price);
        }

    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public PastOrderListAdapter(Activity activity, ArrayList<PastOrderProduct> products) {
        pastOrderProducts = products;
        this.mActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PastOrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_buy_goods, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textName.setText(pastOrderProducts.get(position).getName());
        holder.textPrice.setText("$" + Integer.toString(pastOrderProducts.get(position).getPrice()) + " x " + Integer.toString(pastOrderProducts.get(position).getQuantity()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return pastOrderProducts.size();
    }
}