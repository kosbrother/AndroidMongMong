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
import com.kosbrother.mongmongwoo.utils.TextViewUtil;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private final List<Product> productList;
    private final MyCollectListener listener;
    private Context context;

    public FavoriteAdapter(List<Product> productList, MyCollectListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_favorite, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(FavoriteAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productNameTextView.setText(product.getName());

        holder.productPriceTextView.setText(product.getFinalPriceText());

        holder.specialPriceTextView.setText(product.getSpecialPriceText());
        if (product.isSpecial()) {
            TextViewUtil.paintLineThroughTextView(holder.specialPriceTextView);
        }

        Glide.with(context)
                .load(product.getCover().getUrl())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_rectangle)
                .into(holder.productImageView);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface MyCollectListener {

        void onCollectItemClick(int position);

        void onCancelCollectClick(int adapterPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImageView;
        private final ImageView collectImageView;
        private final TextView productNameTextView;
        private final TextView productPriceTextView;
        private final TextView specialPriceTextView;

        public ViewHolder(View itemView, final MyCollectListener listener) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCollectItemClick(getAdapterPosition());
                }
            });

            productImageView = (ImageView) itemView.findViewById(R.id.item_favorite_product_iv);
            collectImageView = (ImageView) itemView.findViewById(R.id.item_favorite_collect_iv);
            collectImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancelCollectClick(getAdapterPosition());
                }
            });
            productNameTextView = (TextView) itemView.findViewById(R.id.item_favorite_product_name_tv);
            productPriceTextView = (TextView) itemView.findViewById(R.id.item_favorite_product_price_tv);
            specialPriceTextView = (TextView) itemView.findViewById(R.id.item_favorite_special_price_tv);
        }
    }
}
