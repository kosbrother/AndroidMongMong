package com.jasonko.mongmongwoo.adpters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jasonko.mongmongwoo.R;
import com.jasonko.mongmongwoo.ShoppingCarPreference;
import com.jasonko.mongmongwoo.fragments.PurchaseFragment1;
import com.jasonko.mongmongwoo.model.Product;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/9/16.
 */
public class ShoppingCarGoodsAdapter extends RecyclerView.Adapter<ShoppingCarGoodsAdapter.ViewHolder> {

    public ArrayList<Product> shoppingProducts;
    public Activity mActivity;
    public PurchaseFragment1 fragment1;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView textName;
        public TextView textPrice;
        public ImageView imageView;
        public Button deleteButton;
        public Button selectCountButton;

        public ViewHolder(View v) {
            super(v);
            mView = v;
            textName = (TextView) mView.findViewById(R.id.item_car_name);
            textPrice = (TextView) mView.findViewById(R.id.item_car_price);
            imageView = (ImageView) mView.findViewById(R.id.item_car_imageview);
            deleteButton = (Button) mView.findViewById(R.id.item_car_delete_button);
            selectCountButton = (Button) mView.findViewById(R.id.item_car_count_button);
        }

    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public ShoppingCarGoodsAdapter(Activity activity, ArrayList<Product> products, PurchaseFragment1 fragment1) {
        shoppingProducts = products;
        this.mActivity = activity;
        this.fragment1 = fragment1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ShoppingCarGoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_buy_goods, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textName.setText(shoppingProducts.get(position).getName());
        holder.textPrice.setText("$" + Integer.toString(shoppingProducts.get(position).getPrice()));
        Glide.with(mActivity)
                .load(shoppingProducts.get(position).getPic_url())
                .centerCrop()
                .placeholder(R.drawable.icon_head)
                .crossFade()
                .into(holder.imageView);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingCarPreference prefs = new ShoppingCarPreference();
                prefs.removeShoppingItem(mActivity, position);
                fragment1.updateRecycleView();
            }
        });

        holder.selectCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectCountDialog(position);
            }
        });

        holder.selectCountButton.setText("數量 "+ Integer.toString(shoppingProducts.get(position).getBuy_count()));

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

    int tempCount;

    public void showSelectCountDialog(final int product_position){

        tempCount = shoppingProducts.get(product_position).getBuy_count();

        View view = LayoutInflater.from(mActivity)
                .inflate(R.layout.dialog_select_item_counts, null, false);
        Button minusButton = (Button) view.findViewById(R.id.minus_button);
        Button plusButton = (Button) view.findViewById(R.id.plus_button);
        final TextView countText = (TextView) view.findViewById(R.id.count_text_view);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempCount != 1){
                    tempCount = tempCount -1;
                    countText.setText(Integer.toString(tempCount));
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                countText.setText(Integer.toString(tempCount));
            }
        });
        countText.setText(Integer.toString(tempCount));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);

        alertDialogBuilder.setTitle("選擇商品數量");
        alertDialogBuilder.setView(view);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                shoppingProducts.get(product_position).setBuy_count(tempCount);
                ShoppingCarPreference pref = new ShoppingCarPreference();
                for (int i = 0; i < shoppingProducts.size(); i++) {
                    pref.removeShoppingItem(mActivity, i);
                }
                for (int i = 0; i < shoppingProducts.size(); i++) {
                    pref.addShoppingItem(mActivity, shoppingProducts.get(i));
                }
                fragment1.updateRecycleView();
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();

    }

}
