package com.kosbrother.mongmongwoo.adpters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.ArrayList;

public class ShoppingCarListBuyGoodsAdapter extends RecyclerView.Adapter<ShoppingCarListBuyGoodsAdapter.ViewHolder> {

    private ArrayList<Product> productList;

    public ShoppingCarListBuyGoodsAdapter(ArrayList<Product> productList) {
        this.productList = productList;
    }

    @Override
    public ShoppingCarListBuyGoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Product product = productList.get(position);

        holder.productNameTextView.setText(getProductName(product));
        holder.productPriceTextView.setText(getProductPrice(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private String getProductName(Product product) {
        return product.getName() + " - " + product.getSelectedSpec().getStyle();
    }

    private String getProductPrice(Product product) {
        return "$" + product.getPrice() + " x " + product.getBuy_count();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView productNameTextView;
        public final TextView productPriceTextView;

        public ViewHolder(View v) {
            super(v);
            productNameTextView = (TextView) v.findViewById(R.id.product_name_tv);
            productPriceTextView = (TextView) v.findViewById(R.id.product_price_tv);
        }
    }
}

