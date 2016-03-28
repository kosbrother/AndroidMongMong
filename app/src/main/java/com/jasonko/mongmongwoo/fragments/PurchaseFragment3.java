package com.jasonko.mongmongwoo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jasonko.mongmongwoo.R;
import com.jasonko.mongmongwoo.ShoppingCarActivity;
import com.jasonko.mongmongwoo.ShoppingCarPreference;
import com.jasonko.mongmongwoo.adpters.ShoppingCarListBuyGoodsAdapter;
import com.jasonko.mongmongwoo.api.DensityApi;
import com.jasonko.mongmongwoo.api.OrderApi;
import com.jasonko.mongmongwoo.model.Order;
import com.jasonko.mongmongwoo.model.Product;

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

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_buy_goods);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        ShoppingCarListBuyGoodsAdapter adapter = new ShoppingCarListBuyGoodsAdapter(getActivity(),new ArrayList<Product>());
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

            ViewGroup.LayoutParams params= recyclerView.getLayoutParams();
            params.height= (int) DensityApi.convertDpToPixel(50 * theOrder.getOrderProducts().size(), getActivity());
            recyclerView.setLayoutParams(params);

            ShoppingCarListBuyGoodsAdapter adapter = new ShoppingCarListBuyGoodsAdapter(getActivity(),theOrder.getOrderProducts());
            recyclerView.setAdapter(adapter);
        } else {
            //相当于Fragment的onPause
        }
    }

    private class NewsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            OrderApi.httpPostOrder("1153503537995545",theOrder.getProductPrice(), theOrder.getShipPrice(), theOrder.getTotalPrice(), theOrder.getShippingName(), theOrder.getShippingPhone(), "54482", theOrder.getShippingStore().getStore_id(), theOrder.getOrderProducts());
            return true;
        }

        @Override
        protected void onPostExecute(Object result) {
            if ((boolean)result == true){
                Toast.makeText(getActivity(), "訂單成功送出", Toast.LENGTH_SHORT).show();
                ShoppingCarPreference prefs = new ShoppingCarPreference();
                prefs.removeAllShoppingItems(getActivity());
                getActivity().finish();
            }
        }
    }

}
