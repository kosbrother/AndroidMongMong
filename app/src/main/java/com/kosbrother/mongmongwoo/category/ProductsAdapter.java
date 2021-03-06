package com.kosbrother.mongmongwoo.category;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.TextViewUtil;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private List<Product> productList;
    private GoodsGridAdapterListener listener;

    public ProductsAdapter(List<Product> productList, GoodsGridAdapterListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Product theProduct = productList.get(position);

        Glide.with(holder.goodsImageView.getContext())
                .load(theProduct.getCover().getUrl())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_square)
                .into(holder.goodsImageView);

        holder.nameTextView.setText(theProduct.getName());

        holder.priceTextView.setText(theProduct.getFinalPriceText());

        TextView originalPriceTextView = holder.originalPriceTextView;
        originalPriceTextView.setText(theProduct.getOriginalPriceText());
        String discountIconUrl = theProduct.getDiscountIconUrl();
        if (discountIconUrl != null) {
            Glide.with(holder.discountIconImageView.getContext())
                    .load(discountIconUrl)
                    .centerCrop()
                    .placeholder(R.mipmap.img_pre_load_square)
                    .into(holder.discountIconImageView);
            holder.discountIconImageView.setVisibility(View.VISIBLE);
        } else {
            holder.discountIconImageView.setVisibility(View.GONE);
        }

        if (theProduct.isSpecial()) {
            TextViewUtil.paintLineThroughTextView(originalPriceTextView);
        } else {
            TextViewUtil.removeLineThroughTextView(originalPriceTextView);
        }

        holder.setCallBackListener(theProduct.getId(), listener);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView goodsImageView;
        ImageView discountIconImageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView originalPriceTextView;
        LinearLayout addToShoppingCarButton;

        public ViewHolder(View itemView) {
            super(itemView);
            goodsImageView = (ImageView) itemView.findViewById(R.id.item_product_iv);
            discountIconImageView = (ImageView) itemView.findViewById(R.id.item_product_special_price_iv);
            nameTextView = (TextView) itemView.findViewById(R.id.item_product_name_tv);
            priceTextView = (TextView) itemView.findViewById(R.id.item_product_price_tv);
            originalPriceTextView =
                    (TextView) itemView.findViewById(R.id.item_product_original_price_tv);
            addToShoppingCarButton =
                    (LinearLayout) itemView.findViewById(R.id.item_product_add_to_shopping_car_ll);
        }

        public void setCallBackListener(final int productId, final GoodsGridAdapterListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onGoodsItemClick(getAdapterPosition());
                }
            });

            addToShoppingCarButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAddShoppingCartButtonClick(productId, getAdapterPosition());
                }
            });
        }
    }

    public interface GoodsGridAdapterListener {

        void onAddShoppingCartButtonClick(int productId, int position);

        void onGoodsItemClick(int position);
    }

}
