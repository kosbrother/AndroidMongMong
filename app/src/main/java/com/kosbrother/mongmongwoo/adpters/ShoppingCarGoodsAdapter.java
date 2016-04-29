package com.kosbrother.mongmongwoo.adpters;

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
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.ShoppingCarPreference;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment1;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.ArrayList;

public class ShoppingCarGoodsAdapter extends RecyclerView.Adapter<ShoppingCarGoodsAdapter.ViewHolder> {

    public ArrayList<Product> shoppingProducts;
    public Activity mActivity;
    public PurchaseFragment1 fragment1;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShoppingCarGoodsAdapter(Activity activity, ArrayList<Product> products, PurchaseFragment1 fragment1) {
        shoppingProducts = products;
        this.mActivity = activity;
        this.fragment1 = fragment1;
    }

    @Override
    public ShoppingCarGoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_buy_goods, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textName.setText(shoppingProducts.get(position).getName() + " - " + shoppingProducts.get(position).getSelectedSpec().getStyle());
        holder.textPrice.setText("$" + Integer.toString(shoppingProducts.get(position).getPrice()));
        Glide.with(mActivity)
                .load(shoppingProducts.get(position).getSelectedSpec().getPic_url())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_rectangle)
                .into(holder.imageView);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingCarPreference prefs = new ShoppingCarPreference();
                prefs.removeShoppingItem(mActivity, position);
                updateFragment1();
            }
        });

        holder.selectCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = shoppingProducts.get(position).getBuy_count();
                showSelectCountDialog(position);
            }
        });

        holder.selectCountButton.setText("數量 " + Integer.toString(shoppingProducts.get(position).getBuy_count()));

    }

    @Override
    public int getItemCount() {
        return shoppingProducts.size();
    }

    int tempCount;

    private void showSelectCountDialog(final int product_position) {
        View dialogView = getDialogView();
        getAlertDialog(product_position, dialogView).show();
    }

    private View getDialogView() {
        View view = LayoutInflater.from(mActivity)
                .inflate(R.layout.dialog_select_item_counts, null, false);
        final TextView countText = (TextView) view.findViewById(R.id.count_text_view);
        countText.setText(Integer.toString(tempCount));
        setMinusButton(view, countText);
        setPlusButton(view, countText);
        return view;
    }

    private void setMinusButton(View view, final TextView countText) {
        Button minusButton = (Button) view.findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempCount != 1) {
                    tempCount = tempCount - 1;
                    countText.setText(Integer.toString(tempCount));
                }
            }
        });
    }

    private void setPlusButton(View view, final TextView countText) {
        Button plusButton = (Button) view.findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                countText.setText(Integer.toString(tempCount));
            }
        });
    }

    private AlertDialog getAlertDialog(final int product_position, View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);

        alertDialogBuilder.setTitle("選擇商品數量");
        alertDialogBuilder.setView(view);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                shoppingProducts.get(product_position).setBuy_count(tempCount);
                ShoppingCarPreference pref = new ShoppingCarPreference();
                for (int i = 0; i < shoppingProducts.size(); i++) {
                    pref.removeShoppingItem(mActivity, shoppingProducts.size() - 1 - i);
                }
                for (int i = 0; i < shoppingProducts.size(); i++) {
                    pref.addShoppingItem(mActivity, shoppingProducts.get(i));
                }
                updateFragment1();
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();
            }
        });

        return alertDialogBuilder.create();
    }

    private void updateFragment1() {
        fragment1.loadShoppingCart();
        fragment1.updateRecycleView();
        fragment1.updatePricesText();
    }

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

}
