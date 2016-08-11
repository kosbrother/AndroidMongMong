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
import com.kosbrother.mongmongwoo.model.Spec;
import com.kosbrother.mongmongwoo.utils.TextViewUtil;

import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {
    private final List<Product> productList;
    private List<Spec> specs;
    private final WishListListener listener;
    private Context context;

    public WishListAdapter(List<Product> productList, List<Spec> specs, WishListListener listener) {
        this.productList = productList;
        this.specs = specs;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_wish_list, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(WishListAdapter.ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productNameTextView.setText(product.getName());

        holder.finalPriceTextView.setText(product.getFinalPriceText());

        holder.specialPriceTextView.setText(product.getSpecialPriceText());
        if (product.isSpecial()) {
            TextViewUtil.paintLineThroughTextView(holder.specialPriceTextView);
        }

        Spec spec = specs.get(position);

        Glide.with(context)
                .load(spec.getStylePic().getUrl())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_rectangle)
                .into(holder.specImageView);
        holder.specTextView.setText(spec.getStyle());
        holder.stockAmountTextView.setText(spec.getCurrentStockText());
        holder.goShoppingTextView.setVisibility(spec.getStockAmount() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface WishListListener {

        void onWishItemClick(int position);

        void onDeleteWishItemClick(int adapterPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView specImageView;
        private final ImageView notifyImageView;
        private final TextView productNameTextView;
        private final TextView finalPriceTextView;
        private final TextView specialPriceTextView;
        private final TextView specTextView;
        private final TextView stockAmountTextView;
        private final View goShoppingTextView;

        public ViewHolder(View itemView, final WishListListener listener) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onWishItemClick(getAdapterPosition());
                }
            });

            specImageView = (ImageView) itemView.findViewById(R.id.item_wish_list_product_iv);
            notifyImageView = (ImageView) itemView.findViewById(R.id.item_wish_list_notify_iv);
            notifyImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteWishItemClick(getAdapterPosition());
                }
            });
            productNameTextView = (TextView) itemView.findViewById(R.id.item_wish_list_product_name_tv);
            finalPriceTextView = (TextView) itemView.findViewById(R.id.item_wish_list_final_price_tv);
            specialPriceTextView = (TextView) itemView.findViewById(R.id.item_wish_list_special_price_tv);
            specTextView = (TextView) itemView.findViewById(R.id.item_wish_list_spec_tv);
            stockAmountTextView = (TextView) itemView.findViewById(R.id.item_wish_list_stock_amount_tv);
            goShoppingTextView= itemView.findViewById(R.id.item_wish_list_go_shopping_tv);
        }
    }
}
