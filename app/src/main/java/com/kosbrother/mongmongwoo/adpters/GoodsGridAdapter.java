package com.kosbrother.mongmongwoo.adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.TextViewUtil;

import java.util.List;

public class GoodsGridAdapter extends BaseAdapter {

    private Context context;
    private List<Product> productList;
    private GoodsGridAdapterListener listener;

    public GoodsGridAdapter(Context context,
                            List<Product> productList,
                            GoodsGridAdapterListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_goods, null);

            viewHolder = new ViewHolder();
            viewHolder.goodsImageView = (ImageView) convertView.findViewById(R.id.item_goods_iv);
            viewHolder.specialPriceImageView = (ImageView) convertView.findViewById(R.id.item_goods_special_price_iv);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.item_goods_name_tv);
            viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.item_goods_price_tv);
            viewHolder.originalPriceTextView =
                    (TextView) convertView.findViewById(R.id.item_goods_original_price_tv);
            viewHolder.addToShoppingCarButton =
                    (LinearLayout) convertView.findViewById(R.id.item_goods_add_to_shopping_car_ll);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Product theProduct = productList.get(position);

        Glide.with(context)
                .load(theProduct.getCover().getUrl())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_rectangle)
                .into(viewHolder.goodsImageView);

        viewHolder.nameTextView.setText(theProduct.getName());

        viewHolder.priceTextView.setText(theProduct.getFinalPriceText());

        TextView originalPriceTextView = viewHolder.originalPriceTextView;
        originalPriceTextView.setText(theProduct.getOriginalPriceText());
        if (theProduct.isSpecial()) {
            viewHolder.specialPriceImageView.setVisibility(View.VISIBLE);
            TextViewUtil.paintLineThroughTextView(originalPriceTextView);
        } else {
            viewHolder.specialPriceImageView.setVisibility(View.GONE);
            TextViewUtil.removeLineThroughTextView(originalPriceTextView);
        }

        viewHolder.addToShoppingCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddShoppingCartButtonClick(theProduct.getId(), position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView goodsImageView;
        ImageView specialPriceImageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView originalPriceTextView;
        LinearLayout addToShoppingCarButton;
    }

    public interface GoodsGridAdapterListener {

        void onAddShoppingCartButtonClick(int productId, int position);

    }

}
