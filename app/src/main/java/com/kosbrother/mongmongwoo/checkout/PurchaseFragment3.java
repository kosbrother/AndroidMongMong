package com.kosbrother.mongmongwoo.checkout;

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
import com.kosbrother.mongmongwoo.utils.CalculateUtil;

import java.util.List;

public class PurchaseFragment3 extends Fragment {

    public static final String ARG_SERIALIZABLE_ORDER = "ARG_SERIALIZABLE_ORDER";
    public static final String ARG_SERIALIZABLE_PRODUCTS = "ARG_SERIALIZABLE_PRODUCTS";
    public static final String ARG_SERIALIZABLE_ORDER_PRICE = "ARG_SERIALIZABLE_ORDER_PRICE";

    private OnStep3ButtonClickListener mCallback;

    private Order theOrder;
    private List<Product> products;
    private CalculateUtil.OrderPrice orderPrice;

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
        theOrder = (Order) getArguments().getSerializable(ARG_SERIALIZABLE_ORDER);
        products = (List<Product>) getArguments().getSerializable(ARG_SERIALIZABLE_PRODUCTS);
        orderPrice = (CalculateUtil.OrderPrice) getArguments().getSerializable(ARG_SERIALIZABLE_ORDER_PRICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase3, container, false);

        Button sendButton = (Button) view.findViewById(R.id.fragment_purchase3_send_btn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSendOrderClick();
            }
        });

        String itemsPriceText = "NT$ " + orderPrice.getItemsPrice();
        TextView itemsPriceTextView = (TextView) view.findViewById(R.id.fragment_purchase3_items_price_tv);
        itemsPriceTextView.setText(itemsPriceText);

        String shipFeeText = "NT$ " + orderPrice.getShipFee();
        TextView shipFeeTextView = (TextView) view.findViewById(R.id.fragment_purchase3_ship_fee_tv);
        shipFeeTextView.setText(shipFeeText);

        String totalText = "NT$ " + orderPrice.getTotal();
        TextView totalTextView = (TextView) view.findViewById(R.id.fragment_purchase3_total_tv);
        totalTextView.setText(totalText);

        TextView shipNameTextView = (TextView) view.findViewById(R.id.fragment_purchase3_ship_name_tv);
        shipNameTextView.setText(Settings.getShippingName());

        TextView shipPhoneTextView = (TextView) view.findViewById(R.id.fragment_purchase3_ship_phone_tv);
        shipPhoneTextView.setText(Settings.getShippingPhone());

        TextView shipEmailTextView = (TextView) view.findViewById(R.id.fragment_purchase3_ship_email_tv);
        shipEmailTextView.setText(theOrder.getShipEmail());

        Store savedStore = Settings.getSavedStore();
        if (savedStore != null) {
            TextView shippingStoreNameText = (TextView) view.findViewById(R.id.fragment_purchase3_shipping_store_name_tv);
            shippingStoreNameText.setText(savedStore.getName());

            TextView shippingStoreAddressText = (TextView) view.findViewById(R.id.fragment_purchase3_shipping_store_address_tv);
            shippingStoreAddressText.setText(savedStore.getAddress());
        }

        LinearLayout goodsContainerLinearLayout = (LinearLayout) view.findViewById(R.id.fragment_purchase3_goods_container_ll);
        addGoodsItemView(goodsContainerLinearLayout, products);

        int shoppingPointsAmount = orderPrice.getShoppingPointsAmount();
        if (shoppingPointsAmount > 0) {
            view.findViewById(R.id.fragment_purchase3_shopping_points_ll).setVisibility(View.VISIBLE);

            String shoppingPointsAmountText = "-NT$ " + shoppingPointsAmount;
            TextView shoppingPointsAmountTextView = (TextView) view.findViewById(R.id.fragment_purchase3_shopping_points_amount_tv);
            shoppingPointsAmountTextView.setText(shoppingPointsAmountText);

            String shoppingPointsSubtotalText = "NT$ " + orderPrice.getShoppingPointsSubTotal();
            TextView shoppingPointsSubtotalTextView = (TextView) view.findViewById(R.id.fragment_purchase3_shopping_points_subtotal_tv);
            shoppingPointsSubtotalTextView.setText(shoppingPointsSubtotalText);
        }
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
