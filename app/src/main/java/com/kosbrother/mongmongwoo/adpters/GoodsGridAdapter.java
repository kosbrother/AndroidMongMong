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
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_imageview);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.item_name_text);
            viewHolder.priceTextView = (TextView) convertView.findViewById(R.id.item_price_text);
            viewHolder.specialPriceTextView =
                    (TextView) convertView.findViewById(R.id.item_special_price_tv);
            viewHolder.addShoppingCarButton =
                    (LinearLayout) convertView.findViewById(R.id.add_shopping_car_ll);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Product theProduct = productList.get(position);

        Glide.with(context)
                .load(theProduct.getCover().getUrl())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_rectangle)
                .into(viewHolder.imageView);

        viewHolder.nameTextView.setText(theProduct.getName());

        String priceText = "NT$" + theProduct.getPrice();
        viewHolder.priceTextView.setText(priceText);

        setSpecialPrice(viewHolder.specialPriceTextView, theProduct.getSpecialPrice());

        viewHolder.addShoppingCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddShoppingCartButtonClick(theProduct.getId(), position);
            }
        });

        return convertView;
    }

    private void setSpecialPrice(TextView specialPriceTextView, int specialPrice) {
        String specialPriceText;
        if (specialPrice > 0) {
            specialPriceText = "優惠價NT$" + specialPrice;
            TextViewUtil.paintLineThroughTextView(specialPriceTextView);
        } else {
            specialPriceText = "-優惠價-";
        }

        specialPriceTextView.setText(specialPriceText);
    }

    static class ViewHolder {
        ImageView imageView;
        TextView nameTextView;
        TextView priceTextView;
        TextView specialPriceTextView;
        LinearLayout addShoppingCarButton;
    }

    public interface GoodsGridAdapterListener {

        void onAddShoppingCartButtonClick(int productId, int position);

    }

}
