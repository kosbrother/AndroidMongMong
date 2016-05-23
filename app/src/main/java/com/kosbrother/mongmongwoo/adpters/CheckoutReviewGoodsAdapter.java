package com.kosbrother.mongmongwoo.adpters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.List;

public class CheckoutReviewGoodsAdapter extends RecyclerView.Adapter<CheckoutReviewGoodsAdapter.ViewHolder> {

    private final Context context;
    private final List<Product> productList;

    public CheckoutReviewGoodsAdapter(Context context,
                                      List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public CheckoutReviewGoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_review_goods, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = productList.get(position);

        Glide.with(context)
                .load(product.getSelectedSpec().getPic())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_square)
                .into(holder.goodsImageView);

        String nameString = product.getName();
        holder.goodsNameTextView.setText(nameString);

        String countText = "NT$ " + product.getPrice() + " X " + product.getBuy_count();
        holder.priceAndQuantityTextView.setText(countText);

        String subTotalText = "小計：NT$ " + (product.getBuy_count() * product.getPrice());
        holder.subTotalTextView.setText(subTotalText);

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<Product> orderProducts) {
        productList.clear();
        productList.addAll(orderProducts);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView goodsNameTextView;
        public final ImageView goodsImageView;
        public final TextView priceAndQuantityTextView;
        public final TextView subTotalTextView;

        public ViewHolder(View v) {
            super(v);
            goodsNameTextView = (TextView) v.findViewById(R.id.item_car_name);
            goodsImageView = (ImageView) v.findViewById(R.id.item_car_ig);
            priceAndQuantityTextView = (TextView) v.findViewById(R.id.item_price_and_quantity_tv);
            subTotalTextView = (TextView) v.findViewById(R.id.subtotal_tv);
        }

    }
}
