package com.jasonko.mongmongwoo.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jasonko.mongmongwoo.R;
import com.jasonko.mongmongwoo.Settings;
import com.jasonko.mongmongwoo.ShoppingCarActivity;
import com.jasonko.mongmongwoo.ShoppingCarPreference;
import com.jasonko.mongmongwoo.adpters.ShoppingCarGoodsAdapter;
import com.jasonko.mongmongwoo.api.DensityApi;
import com.jasonko.mongmongwoo.model.Product;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/9/16.
 */
public class PurchaseFragment1 extends Fragment {

    LinearLayout noItemLayout;
    LinearLayout noLoginBuyButtons;
    Button fb_buy_button;
    Button no_name_buy_button;

    ArrayList<Product> shoppingCarProducts;
    ShoppingCarGoodsAdapter adapter;
    RecyclerView newsRecylerView;
    Button deliverButton;
    TextView totalGoodsPriceText;
    int totalGoodsPrice;
    int shippingPrice;
    TextView shippingPriceText;
    TextView totalPriceText;
    Button confirmButton;

    int shippingType = -1; // 0 means 超商取貨付款, 1 means 宅配

  public static PurchaseFragment1 newInstance() {
        PurchaseFragment1 fragment = new PurchaseFragment1();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_purchase1, container, false);

        deliverButton = (Button) view.findViewById(R.id.deliver_button);
        totalGoodsPriceText = (TextView) view.findViewById(R.id.fragment1_goodsTotalPriceText);
        shippingPriceText = (TextView) view.findViewById(R.id.fragment1_shippingPriceText);
        totalPriceText = (TextView) view.findViewById(R.id.fragment1_totalPriceText);
        confirmButton = (Button) view.findViewById(R.id.fragment1_confirm_button);
        noItemLayout = (LinearLayout) view.findViewById(R.id.shopping_car_no_item_layout);
        noLoginBuyButtons = (LinearLayout) view.findViewById(R.id.layout_buy_button);
        fb_buy_button = (Button) view.findViewById(R.id.fragment1_fb_buy_button);
        no_name_buy_button = (Button) view.findViewById(R.id.fragment1_no_name_buy_button);

        shippingPrice = 0;

        newsRecylerView = (RecyclerView) view.findViewById(R.id.recycler_buy_goods);
        newsRecylerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newsRecylerView.setLayoutManager(mLayoutManager);

        ShoppingCarPreference pref = new ShoppingCarPreference();
        shoppingCarProducts =  pref.loadShoppingItems(getActivity());

        if (shoppingCarProducts.size() == 0){
            noItemLayout.setVisibility(View.VISIBLE);
            ShoppingCarActivity activity = (ShoppingCarActivity)getActivity();
            activity.setBreadCurmbsVisibility(View.INVISIBLE);
        }else {
            noItemLayout.setVisibility(View.GONE);
            ShoppingCarActivity activity = (ShoppingCarActivity)getActivity();
            activity.setBreadCurmbsVisibility(View.VISIBLE);
        }

        if (!Settings.checkIsLogIn(getActivity())){
            noLoginBuyButtons.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.GONE);
        }

        fb_buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call shopping car login and turn page
                if (shippingType != -1) {
                    // save to activity order
                    ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
                    activity.saveOrderProducts(shoppingCarProducts);
                    activity.getOrder().setShipPrice(shippingPrice);
                    activity.getOrder().setProductPrice(totalGoodsPrice);
                    activity.getOrder().setTotalPrice(shippingPrice+totalGoodsPrice);
                    activity.performClickFbButton();
                }else {
                    Toast.makeText(getActivity(),"請選擇運送方式", Toast.LENGTH_SHORT).show();
                }
            }
        });

        no_name_buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shippingType != -1) {
                    // save to activity order
                    ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
                    activity.saveOrderProducts(shoppingCarProducts);
                    activity.getOrder().setShipPrice(shippingPrice);
                    activity.getOrder().setProductPrice(totalGoodsPrice);
                    activity.getOrder().setTotalPrice(shippingPrice+totalGoodsPrice);
                    activity.setPagerPostition(1);
                }else {
                    Toast.makeText(getActivity(),"請選擇運送方式", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewGroup.LayoutParams params= newsRecylerView.getLayoutParams();
        params.height= (int) DensityApi.convertDpToPixel(100* shoppingCarProducts.size(),getActivity());
        newsRecylerView.setLayoutParams(params);

        adapter = new ShoppingCarGoodsAdapter(getActivity(), shoppingCarProducts, this);
        newsRecylerView.setAdapter(adapter);

        setPricesText();

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("選擇運送方式");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("全家,OK,萊爾富超商取貨付款 $60");
        arrayAdapter.add("宅配貨到付款 $150");

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deliverButton.setText(arrayAdapter.getItem(which));
                        switch (which) {
                            case 0:
                                shippingPrice = 60;
                                shippingType = 0;
                                break;
                            case 1:
                                shippingPrice = 150;
                                shippingType = 1;
                                break;
                        }
                        shippingPriceText.setText("$" + Integer.toString(shippingPrice));
                        setPricesText();
                    }
                });

        deliverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderSingle.show();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shippingType != -1) {
                    // save to activity order
                    ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
                    activity.saveOrderProducts(shoppingCarProducts);
                    activity.getOrder().setShipPrice(shippingPrice);
                    activity.getOrder().setProductPrice(totalGoodsPrice);
                    activity.getOrder().setTotalPrice(shippingPrice+totalGoodsPrice);
                    activity.setPagerPostition(1);
                }else {
                    Toast.makeText(getActivity(),"請選擇運送方式", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void setPricesText() {
        totalGoodsPrice = 0;
        for (int i = 0; i< shoppingCarProducts.size(); i++){
            totalGoodsPrice = totalGoodsPrice + shoppingCarProducts.get(i).getPrice() * shoppingCarProducts.get(i).getBuy_count();
        }
        totalGoodsPriceText.setText("$"+ Integer.toString(totalGoodsPrice));
        totalPriceText.setText("$"+Integer.toString(totalGoodsPrice+shippingPrice));
    }

    public void updateRecycleView(){
        ShoppingCarPreference pref = new ShoppingCarPreference();
        shoppingCarProducts =  pref.loadShoppingItems(this.getActivity());

        ViewGroup.LayoutParams params= newsRecylerView.getLayoutParams();
        params.height= (int) DensityApi.convertDpToPixel(100* shoppingCarProducts.size(),getActivity());
        newsRecylerView.setLayoutParams(params);

        adapter = new ShoppingCarGoodsAdapter(getActivity(), shoppingCarProducts, this);
        newsRecylerView.setAdapter(adapter);

        if (shoppingCarProducts.size() == 0){
            noItemLayout.setVisibility(View.VISIBLE);
            ShoppingCarActivity activity = (ShoppingCarActivity)getActivity();
            activity.setBreadCurmbsVisibility(View.INVISIBLE);
        }
    }

}
