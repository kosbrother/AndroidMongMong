package com.kosbrother.mongmongwoo.adpters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.ArrayList;

public class ShoppingCarGoodsAdapter extends RecyclerView.Adapter<ShoppingCarGoodsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Product> productList;
    private final ShoppingCartGoodsListener listener;

    public ShoppingCarGoodsAdapter(Context context,
                                   ArrayList<Product> productList,
                                   ShoppingCartGoodsListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @Override
    public ShoppingCarGoodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_buy_goods, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = productList.get(position);

        String nameString = product.getName() + " - " + product.getSelectedSpec().getStyle();
        holder.goodsNameTextView.setText(nameString);

        String priceString = "$" + product.getPrice();
        holder.priceTextView.setText(priceString);

        Glide.with(context)
                .load(product.getSelectedSpec().getPic_url())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_square)
                .into(holder.goodsImageView);

        String countText = "數量 " + product.getBuy_count();
        holder.selectCountButton.setText(countText);

        holder.setDeleteListener(listener);
        holder.setSelectCountListener(listener, product.getBuy_count());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(ArrayList<Product> productList) {
        this.productList.clear();
        this.productList.addAll(productList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView goodsNameTextView;
        public final TextView priceTextView;
        public final ImageView goodsImageView;
        public final Button deleteButton;
        public final Button selectCountButton;

        public ViewHolder(View v) {
            super(v);
            goodsNameTextView = (TextView) v.findViewById(R.id.item_car_name);
            priceTextView = (TextView) v.findViewById(R.id.item_car_price);
            goodsImageView = (ImageView) v.findViewById(R.id.item_car_imageview);
            deleteButton = (Button) v.findViewById(R.id.item_car_delete_button);
            selectCountButton = (Button) v.findViewById(R.id.item_car_count_button);
        }

        public void setDeleteListener(final ShoppingCartGoodsListener listener) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteButtonClick(getAdapterPosition());
                }
            });
        }

        public void setSelectCountListener(final ShoppingCartGoodsListener listener, final int buy_count) {
            selectCountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelectCountButtonClick(getAdapterPosition(), buy_count);
                }
            });
        }
    }

    public interface ShoppingCartGoodsListener {

        void onDeleteButtonClick(int position);

        void onSelectCountButtonClick(int position, int tempCount);
    }

}
