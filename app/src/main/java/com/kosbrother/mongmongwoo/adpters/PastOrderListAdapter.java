package com.kosbrother.mongmongwoo.adpters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.QueryOrder;

import java.util.List;

public class PastOrderListAdapter extends RecyclerView.Adapter<PastOrderListAdapter.ViewHolder> {

    private final List<QueryOrder.PastItemEntity> productList;

    public PastOrderListAdapter(List<QueryOrder.PastItemEntity> productList) {
        this.productList = productList;
    }

    @Override
    public PastOrderListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QueryOrder.PastItemEntity product = productList.get(position);

        holder.productNameTextView.setText(product.getName());
        holder.productPriceTextView.setText(getProductPrice(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private String getProductPrice(QueryOrder.PastItemEntity postProduct) {
        return "$" + postProduct.getPrice() + " x " + postProduct.getQuantity();
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
