package com.kosbrother.mongmongwoo.fragments;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.ShoppingCarPreference;
import com.kosbrother.mongmongwoo.adpters.CheckoutReviewGoodsAdapter;
import com.kosbrother.mongmongwoo.api.OrderApi;
import com.kosbrother.mongmongwoo.gcm.GcmPreferences;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep3ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.model.Order;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.CalculateUtil;

import java.util.ArrayList;

public class PurchaseFragment3 extends Fragment {

    private ScrollView scrollView;
    RecyclerView recyclerView;
    TextView shippingPriceText;
    TextView totalPriceText;
    TextView shippingNameText;
    TextView shippingPhoneText;
    TextView shippingStoreNameText;
    TextView shippingStoreAddressText;
    private TextView totalGoodsPriceTextView;
    private TextView emailTextView;
    Button sendButton;

    Order theOrder;
    ProgressBar progressBar;

    public static PurchaseFragment3 newInstance() {
        return new PurchaseFragment3();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_purchase3, container, false);
        scrollView = (ScrollView) view.findViewById(R.id.purchase3_sv);
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

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_buy_goods);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        CheckoutReviewGoodsAdapter adapter = new CheckoutReviewGoodsAdapter(
                getContext(), new ArrayList<Product>());
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CheckoutStep3ClickEvent(GALabel.SEND_ORDER));
                new NewsTask().execute();
            }
        });

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
            theOrder = activity.getOrder();

            int totalGoodsPrice = CalculateUtil.calculateTotalGoodsPrice(theOrder.getOrderProducts());
            String totalGoodsPriceString = "" + totalGoodsPrice;
            totalGoodsPriceTextView.setText(totalGoodsPriceString);

            int shipPrice = theOrder.getShipPrice();
            if (shipPrice == 0) {
                shippingPriceText.setText("免運費");
            } else {
                String shippingPriceString = "" + shipPrice;
                shippingPriceText.setText(shippingPriceString);
            }

            String totalPriceString = "NT$ " + theOrder.getTotalPrice();
            totalPriceText.setText(totalPriceString);

            shippingNameText.setText(theOrder.getShippingName());
            shippingPhoneText.setText(theOrder.getShippingPhone());
            shippingStoreNameText.setText(theOrder.getShippingStore().getName());
            shippingStoreAddressText.setText(theOrder.getShippingStore().getAddress());
            emailTextView.setText(theOrder.getShippingEmail());

            CheckoutReviewGoodsAdapter adapter = (CheckoutReviewGoodsAdapter) recyclerView.getAdapter();
            adapter.updateProducts(theOrder.getOrderProducts());
            scrollView.smoothScrollTo(0, 0);
        } else {
            //相当于Fragment的onPause
        }
    }

    private class NewsTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            if (theOrder.getOrderProducts() == null || theOrder.getOrderProducts().size() == 0) {
                Toast.makeText(getActivity(), "購物車商品資料錯誤,請聯絡客服LINE@,感謝您^^", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String message = OrderApi.httpPostOrder(
                    theOrder.getUid(), theOrder.getProductPrice(),
                    theOrder.getShipPrice(), theOrder.getTotalPrice(),
                    theOrder.getShippingName(), theOrder.getShippingPhone(),
                    theOrder.getShippingStore().getStore_code(),
                    theOrder.getShippingStore().getName(),
                    theOrder.getShippingStore().getStore_id(),
                    theOrder.getOrderProducts(),
                    theOrder.getShippingEmail(),
                    sharedPreferences.getString(GcmPreferences.TOKEN, ""));
            return !message.contains("Error");
        }

        @Override
        protected void onPostExecute(Object result) {
            progressBar.setVisibility(View.GONE);
            if ((boolean) result == true) {
                Settings.saveUserStoreData(theOrder.getShippingStore());
                Settings.saveUserShippingNameAndPhone(theOrder.getShippingName(), theOrder.getShippingPhone());

                ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
                new ShoppingCarPreference().removeAllShoppingItems(activity);
                activity.startPurchaseFragment4();
            } else {
                Toast.makeText(getActivity(), "訂單未成功送出 資料異常", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
