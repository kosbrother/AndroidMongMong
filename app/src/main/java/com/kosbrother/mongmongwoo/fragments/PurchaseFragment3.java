package com.kosbrother.mongmongwoo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep3EnterEvent;
import com.kosbrother.mongmongwoo.model.Order;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Store;

import java.util.List;

public class PurchaseFragment3 extends Fragment {

    public static final String ARG_SERIALIZABLE_ORDER = "ARG_SERIALIZABLE_ORDER";
    public static final String ARG_SERIALIZABLE_PRODUCTS = "ARG_SERIALIZABLE_PRODUCTS";

    private OnStpe3ButtonClickListener mCallback;

    private Order theOrder;
    private List<Product> products;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStpe3ButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnStep3ButtonClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theOrder = (Order) getArguments().getSerializable(ARG_SERIALIZABLE_ORDER);
        products = (List<Product>) getArguments().getSerializable(ARG_SERIALIZABLE_PRODUCTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase3, container, false);

        Button sendButton = (Button) view.findViewById(R.id.fragment3_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSendOrderClick();
            }
        });

        TextView totalGoodsPriceTextView = (TextView) view.findViewById(R.id.fragment3_total_goods_price_text);
        String totalGoodsPriceString = "" + theOrder.getItemsPrice();
        totalGoodsPriceTextView.setText(totalGoodsPriceString);

        TextView shippingPriceText = (TextView) view.findViewById(R.id.fragment3_shipping_price_text);
        int shipPrice = theOrder.getShipFee();
        if (shipPrice == 0) {
            shippingPriceText.setText("免運費");
        } else {
            String shippingPriceString = "" + shipPrice;
            shippingPriceText.setText(shippingPriceString);
        }

        String totalPriceString = "NT$ " + theOrder.getTotal();
        TextView totalPriceText = (TextView) view.findViewById(R.id.fragment3_total_price_text);
        totalPriceText.setText(totalPriceString);

        TextView shippingNameText = (TextView) view.findViewById(R.id.fragment3_shipping_name_text);
        shippingNameText.setText(Settings.getShippingName());

        TextView shippingPhoneText = (TextView) view.findViewById(R.id.fragment3_shipping_phone_text);
        shippingPhoneText.setText(Settings.getShippingPhone());

        TextView emailTextView = (TextView) view.findViewById(R.id.fragment3_email_text);
        emailTextView.setText(theOrder.getShipEmail());

        Store savedStore = Settings.getSavedStore();
        if (savedStore != null) {
            TextView shippingStoreNameText = (TextView) view.findViewById(R.id.fragment3_shipping_store_name_text);
            shippingStoreNameText.setText(savedStore.getName());

            TextView shippingStoreAddressText = (TextView) view.findViewById(R.id.fragment3_shipping_store_address_text);
            shippingStoreAddressText.setText(savedStore.getAddress());
        }

        LinearLayout goodsContainerLinearLayout = (LinearLayout) view.findViewById(R.id.goods_container_ll);
        addGoodsItemView(goodsContainerLinearLayout, products);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GAManager.sendEvent(new CheckoutStep3EnterEvent());
    }

    private void addGoodsItemView(LinearLayout goodsContainerLinearLayout, List<Product> orderProducts) {
        goodsContainerLinearLayout.removeAllViews();

        for (Product product : orderProducts) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_checkout_review_goods, null);
            goodsContainerLinearLayout.addView(itemView);

            ImageView goodsImageView = (ImageView) itemView.findViewById(R.id.item_car_ig);
            Glide.with(getContext())
                    .load(product.getSelectedSpec().getStylePic().getUrl())
                    .centerCrop()
                    .placeholder(R.mipmap.img_pre_load_square)
                    .into(goodsImageView);

            TextView goodsNameTextView = (TextView) itemView.findViewById(R.id.item_car_name);
            goodsNameTextView.setText(product.getName());

            TextView styleTextView = (TextView) itemView.findViewById(R.id.item_style_tv);
            styleTextView.setText(product.getSelectedSpec().getStyle());

            String countText = "NT$ " + product.getFinalPrice() + " X " + product.getBuy_count();
            TextView priceAndQuantityTextView = (TextView) itemView.findViewById(R.id.item_price_and_quantity_tv);
            priceAndQuantityTextView.setText(countText);

            String subTotalText = "小計：NT$ " + (product.getBuy_count() * product.getFinalPrice());
            TextView subTotalTextView = (TextView) itemView.findViewById(R.id.subtotal_tv);
            subTotalTextView.setText(subTotalText);
        }
    }

    public interface OnStpe3ButtonClickListener {

        void onSendOrderClick();
    }

}
