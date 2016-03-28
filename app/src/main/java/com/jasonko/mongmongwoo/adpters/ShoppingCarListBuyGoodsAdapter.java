package com.jasonko.mongmongwoo.adpters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonko.mongmongwoo.R;
import com.jasonko.mongmongwoo.model.Product;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/15/16.
 */
public class ShoppingCarListBuyGoodsAdapter extends RecyclerView.Adapter<ShoppingCarListBuyGoodsAdapter.ViewHolder> {

    public ArrayList<Product> shoppingProducts;
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
    public ShoppingCarListBuyGoodsAdapter(Activity activity, ArrayList<Product> products) {
        shoppingProducts = products;
        this.mActivity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ShoppingCarListBuyGoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
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
        holder.textName.setText(shoppingProducts.get(position).getName());
        holder.textPrice.setText("$" + Integer.toString(shoppingProducts.get(position).getPrice()) + " x " + Integer.toString(shoppingProducts.get(position).getBuy_count()));

//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return shoppingProducts.size();
    }
}

