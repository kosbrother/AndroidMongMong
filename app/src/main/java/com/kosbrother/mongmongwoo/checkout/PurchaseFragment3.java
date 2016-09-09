package com.kosbrother.mongmongwoo.checkout;

import android.content.Context;
import android.databinding.DataBindingUtil;
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
import com.kosbrother.mongmongwoo.common.DeliveryUserInfoViewModel;
import com.kosbrother.mongmongwoo.common.OrderPriceViewModel;
import com.kosbrother.mongmongwoo.databinding.FragmentPurchase3Binding;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep3EnterEvent;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.utils.CalculateUtil;

import java.util.List;

public class PurchaseFragment3 extends Fragment {

    public static final String ARG_SERIALIZABLE_PRODUCTS = "ARG_SERIALIZABLE_PRODUCTS";
    public static final String ARG_SERIALIZABLE_ORDER_PRICE = "ARG_SERIALIZABLE_ORDER_PRICE";
    public static final String ARG_SERIALIZABLE_STORE = "ARG_SERIALIZABLE_STORE";
    public static final String ARG_STRING_SHIP_ADDRESS = "ARG_STRING_SHIP_ADDRESS";

    private OnStep3ButtonClickListener mCallback;

    private List<Product> products;
    private CalculateUtil.OrderPrice orderPrice;
    private String shipAddress;
    private Store store;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStep3ButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnStep3ButtonClickListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        products = (List<Product>) getArguments().getSerializable(ARG_SERIALIZABLE_PRODUCTS);
        orderPrice = (CalculateUtil.OrderPrice) getArguments().getSerializable(ARG_SERIALIZABLE_ORDER_PRICE);
        store = (Store) getArguments().getSerializable(ARG_SERIALIZABLE_STORE);
        shipAddress = getArguments().getString(ARG_STRING_SHIP_ADDRESS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentPurchase3Binding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_purchase3, container, false);
        binding.setDeliveryUserInfo(new DeliveryUserInfoViewModel(store, shipAddress));
        binding.setOrderPrice(new OrderPriceViewModel(orderPrice));
        View view = binding.getRoot();

        Button sendButton = (Button) view.findViewById(R.id.fragment_purchase3_send_btn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSendOrderClick();
            }
        });

        LinearLayout goodsContainerLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_purchase3_goods_container_ll);
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

    public interface OnStep3ButtonClickListener {

        void onSendOrderClick();
    }

}
