package com.kosbrother.mongmongwoo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.ShoppingCarPreference;
import com.kosbrother.mongmongwoo.adpters.ShoppingCarListBuyGoodsAdapter;
import com.kosbrother.mongmongwoo.api.OrderApi;
import com.kosbrother.mongmongwoo.model.Order;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/9/16.
 */
public class PurchaseFragment3 extends Fragment {

    RecyclerView recyclerView;
    TextView shippingPriceText;
    TextView totalPriceText;
    TextView shippingNameText;
    TextView shippingPhoneText;
    TextView shippingStoreNameText;
    TextView shippingStoreAddressText;
    Button sendButton;

    Order theOrder;
    ProgressBar progressBar;

    public static PurchaseFragment3 newInstance() {
        PurchaseFragment3 fragment = new PurchaseFragment3();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_purchase3, container, false);
        shippingPriceText = (TextView) view.findViewById(R.id.fragment3_shipping_price_text);
        totalPriceText = (TextView) view.findViewById(R.id.fragment3_total_price_text);
        shippingNameText = (TextView) view.findViewById(R.id.fragment3_shipping_name_text);
        shippingPhoneText = (TextView) view.findViewById(R.id.fragment3_shipping_phone_text);
        shippingStoreNameText = (TextView) view.findViewById(R.id.fragment3_shipping_store_name_text);
        shippingStoreAddressText = (TextView) view.findViewById(R.id.fragment3_shipping_store_address_text);
        sendButton = (Button) view.findViewById(R.id.fragment3_send_button);
        progressBar = (ProgressBar) view.findViewById(R.id.my_progress_bar);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_buy_goods);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        ShoppingCarListBuyGoodsAdapter adapter = new ShoppingCarListBuyGoodsAdapter(new ArrayList<Product>());
        recyclerView.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

            shippingPriceText.setText(Integer.toString(theOrder.getShipPrice()));
            totalPriceText.setText(Integer.toString(theOrder.getTotalPrice()));
            shippingNameText.setText("收件人：" + theOrder.getShippingName());
            shippingPhoneText.setText(theOrder.getShippingPhone());
            shippingStoreNameText.setText(theOrder.getShippingStore().getName());
            shippingStoreAddressText.setText(theOrder.getShippingStore().getAddress());

            ShoppingCarListBuyGoodsAdapter adapter = new ShoppingCarListBuyGoodsAdapter(theOrder.getOrderProducts());
            recyclerView.setAdapter(adapter);
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
            String message = OrderApi.httpPostOrder(
                    theOrder.getUid(), theOrder.getProductPrice(),
                    theOrder.getShipPrice(), theOrder.getTotalPrice(),
                    theOrder.getShippingName(), theOrder.getShippingPhone(),
                    theOrder.getShippingStore().getStore_code(),
                    theOrder.getShippingStore().getName(),
                    theOrder.getShippingStore().getStore_id(),
                    theOrder.getOrderProducts(),
                    theOrder.getShippingEmail());
            return !message.contains("Error");
        }

        @Override
        protected void onPostExecute(Object result) {
            progressBar.setVisibility(View.GONE);
            if ((boolean) result == true) {
                Settings.saveUserStoreData(getActivity(), theOrder.getShippingStore());
                Settings.saveUserShippingNameAndPhone(getActivity(), theOrder.getShippingName(), theOrder.getShippingPhone());

                ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
                new ShoppingCarPreference().removeAllShoppingItems(activity);
                activity.startPurchaseFragment4();
            } else {
                Toast.makeText(getActivity(), "訂單未成功送出 資料異常", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
