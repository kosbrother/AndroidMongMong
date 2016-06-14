package com.kosbrother.mongmongwoo.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.ShoppingCarPreference;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.fcm.FcmPreferences;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep3ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.model.Order;
import com.kosbrother.mongmongwoo.model.PastOrder;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.CalculateUtil;

import java.util.List;

import rx.functions.Action1;

public class PurchaseFragment3 extends Fragment {

    TextView shippingPriceText;
    TextView totalPriceText;
    TextView shippingNameText;
    TextView shippingPhoneText;
    TextView shippingStoreNameText;
    TextView shippingStoreAddressText;
    private TextView totalGoodsPriceTextView;
    private TextView emailTextView;
    Button sendButton;
    private LinearLayout goodsContainerLinearLayout;

    Order theOrder;
    ProgressBar progressBar;

    public static PurchaseFragment3 newInstance() {
        return new PurchaseFragment3();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_purchase3, container, false);
        shippingPriceText = (TextView) view.findViewById(R.id.fragment3_shipping_price_text);
        totalPriceText = (TextView) view.findViewById(R.id.fragment3_total_price_text);
        shippingNameText = (TextView) view.findViewById(R.id.fragment3_shipping_name_text);
        shippingPhoneText = (TextView) view.findViewById(R.id.fragment3_shipping_phone_text);
        shippingStoreNameText = (TextView) view.findViewById(R.id.fragment3_shipping_store_name_text);
        shippingStoreAddressText = (TextView) view.findViewById(R.id.fragment3_shipping_store_address_text);
        totalGoodsPriceTextView = (TextView) view.findViewById(R.id.fragment3_total_goods_price_text);
        emailTextView = (TextView) view.findViewById(R.id.fragment3_email_text);
        sendButton = (Button) view.findViewById(R.id.fragment3_send_button);
        progressBar = (ProgressBar) view.findViewById(R.id.my_progress_bar);
        goodsContainerLinearLayout = (LinearLayout) view.findViewById(R.id.goods_container_ll);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CheckoutStep3ClickEvent(GALabel.SEND_ORDER));
                requestPostOrder();
            }
        });

        return view;
    }

    private void requestPostOrder() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        theOrder.setRegistrationId(sharedPreferences.getString(FcmPreferences.TOKEN, ""));
        String json = new Gson().toJson(theOrder);
        Webservice.postOrder(json, new Action1<ResponseEntity<PastOrder>>() {
            @Override
            public void call(ResponseEntity<PastOrder> stringResponseEntity) {
                PastOrder data = stringResponseEntity.getData();
                if (data == null) {
                    GAManager.sendError("postOrderError", stringResponseEntity.getError());
                    Toast.makeText(getActivity(), "訂單未成功送出 資料異常", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Settings.saveUserStoreData(theOrder.getStore());
                    Settings.saveUserShippingNameAndPhone(theOrder.getShipName(), theOrder.getShipPhone());

                    ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
                    new ShoppingCarPreference().removeAllShoppingItems(activity);
                    activity.startPurchaseFragment4();
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
            theOrder = activity.getOrder();

            int totalGoodsPrice = CalculateUtil.calculateTotalGoodsPrice(activity.getProducts());
            String totalGoodsPriceString = "" + totalGoodsPrice;
            totalGoodsPriceTextView.setText(totalGoodsPriceString);

            int shipPrice = theOrder.getShipFee();
            if (shipPrice == 0) {
                shippingPriceText.setText("免運費");
            } else {
                String shippingPriceString = "" + shipPrice;
                shippingPriceText.setText(shippingPriceString);
            }

            String totalPriceString = "NT$ " + theOrder.getTotal();
            totalPriceText.setText(totalPriceString);

            shippingNameText.setText(theOrder.getShipName());
            shippingPhoneText.setText(theOrder.getShipPhone());
            shippingStoreNameText.setText(theOrder.getStore().getName());
            shippingStoreAddressText.setText(theOrder.getStore().getAddress());
            emailTextView.setText(theOrder.getShipEmail());

            addGoodsItemView(activity.getProducts());
        } else {
            //相当于Fragment的onPause
        }
    }

    private void addGoodsItemView(List<Product> orderProducts) {
        goodsContainerLinearLayout.removeAllViews();

        for (Product product : orderProducts) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_checkout_review_goods, null);
            TextView goodsNameTextView = (TextView) itemView.findViewById(R.id.item_car_name);
            ImageView goodsImageView = (ImageView) itemView.findViewById(R.id.item_car_ig);
            TextView priceAndQuantityTextView = (TextView) itemView.findViewById(R.id.item_price_and_quantity_tv);
            TextView subTotalTextView = (TextView) itemView.findViewById(R.id.subtotal_tv);
            goodsContainerLinearLayout.addView(itemView);

            Glide.with(getContext())
                    .load(product.getSelectedSpec().getStylePic().getUrl())
                    .centerCrop()
                    .placeholder(R.mipmap.img_pre_load_square)
                    .into(goodsImageView);

            String nameString = product.getName();
            goodsNameTextView.setText(nameString);

            String countText = "NT$ " + product.getFinalPrice() + " X " + product.getBuy_count();
            priceAndQuantityTextView.setText(countText);

            String subTotalText = "小計：NT$ " + (product.getBuy_count() * product.getFinalPrice());
            subTotalTextView.setText(subTotalText);
        }
    }

}
