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

public class MyCollectAdapter extends RecyclerView.Adapter<MyCollectAdapter.ViewHolder> {
    private final List<Product> productList;
    private final MyCollectListener listener;
    private Context context;

    public MyCollectAdapter(List<Product> productList, MyCollectListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_my_collect, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(MyCollectAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productNameTextView.setText(product.getName());

        String priceString = "NT$" + product.getPrice();
        holder.productPriceTextView.setText(priceString);

        Glide.with(context)
                .load(product.getPic_url())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_rectangle)
                .into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void update(List<Product> productList) {
        this.productList.clear();
        this.productList.addAll(productList);
    }

    public interface MyCollectListener {

        void onCollectItemClick(int position);

        void onRemoveItemClick(int adapterPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImageView;
        private final ImageView collectImageView;
        private final TextView productNameTextView;
        private final TextView productPriceTextView;

        public ViewHolder(View itemView, final MyCollectListener listener) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCollectItemClick(getAdapterPosition());
                }
            });

            productImageView = (ImageView) itemView.findViewById(R.id.product_iv);
            collectImageView = (ImageView) itemView.findViewById(R.id.collect_iv);
            collectImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRemoveItemClick(getAdapterPosition());
                }
            });
            productNameTextView = (TextView) itemView.findViewById(R.id.product_name_tv);
            productPriceTextView = (TextView) itemView.findViewById(R.id.product_price_tv);

        }
    }
}
