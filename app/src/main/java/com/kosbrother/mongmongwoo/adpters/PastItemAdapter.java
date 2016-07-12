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
import com.kosbrother.mongmongwoo.entity.pastorder.PastItem;

import java.util.List;

public class PastItemAdapter extends RecyclerView.Adapter<PastItemAdapter.ViewHolder> {

    private Context context;
    private final List<PastItem> pastItems;

    public PastItemAdapter(List<PastItem> pastItems) {
        this.pastItems = pastItems;
    }

    @Override
    public PastItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_past_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PastItem item = pastItems.get(position);

        holder.itemNameTextView.setText(item.getName());
        holder.itemPriceTextView.setText(item.getPriceAndQuantityText());
        holder.itemStyleTextView.setText(item.getStyle());
        Glide.with(context)
                .load(item.getStylePic())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_square)
                .into(holder.itemStyleImageView);
    }

    @Override
    public int getItemCount() {
        return pastItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView itemStyleImageView;
        private final TextView itemNameTextView;
        private final TextView itemStyleTextView;
        private final TextView itemPriceTextView;

        public ViewHolder(View v) {
            super(v);
            itemStyleImageView = (ImageView) v.findViewById(R.id.item_style_iv);
            itemNameTextView = (TextView) v.findViewById(R.id.item_name_tv);
            itemStyleTextView = (TextView) v.findViewById(R.id.item_style_tv);
            itemPriceTextView = (TextView) v.findViewById(R.id.item_price_tv);
        }
    }
}
