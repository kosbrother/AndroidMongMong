package com.kosbrother.mongmongwoo.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.ShoppingCarPreference;
import com.kosbrother.mongmongwoo.adpters.ShoppingCarGoodsAdapter;
import com.kosbrother.mongmongwoo.api.DensityApi;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep1ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.ArrayList;

public class PurchaseFragment1 extends Fragment implements ShoppingCarGoodsAdapter.ShoppingCartGoodsListener {

    LinearLayout noItemLayout;
    LinearLayout noLoginLayout;
    Button fb_buy_button;
    Button no_name_buy_button;

    RecyclerView newsRecylerView;
    Button deliverButton;
    TextView totalGoodsPriceText;
    TextView shippingPriceText;
    TextView totalPriceText;
    Button confirmButton;
    TextView no_ship_fee_text;
    TextView ship_text;

    ArrayList<Product> shoppingCarProducts;
    int totalGoodsPrice;
    int shippingPrice;
    int shippingType = -1; // 0 means 超商取貨付款, 1 means 宅配

    private int tempCount;

    public static PurchaseFragment1 newInstance() {
        return new PurchaseFragment1();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadShoppingCart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_purchase1, container, false);
        findView(view);

        initVisibleLayout();
        setRecyclerView();
        setFbBuyButton();
        setNoNameBuyButton();
        setDeliverButton();
        setConfirmButton();

        updatePricesText();
        updateLayoutByLoginStatus();
        return view;
    }

    public void loadShoppingCart() {
        ShoppingCarPreference pref = new ShoppingCarPreference();
        shoppingCarProducts = pref.loadShoppingItems(getContext());
    }

    public void updatePricesText() {
        totalGoodsPrice = 0;
        for (int i = 0; i < shoppingCarProducts.size(); i++) {
            totalGoodsPrice = totalGoodsPrice + shoppingCarProducts.get(i).getPrice() * shoppingCarProducts.get(i).getBuy_count();
        }
        totalGoodsPriceText.setText("$" + Integer.toString(totalGoodsPrice));

        if (shippingType != -1) {
            if (totalGoodsPrice >= 490) {
                shippingPrice = 0;
                no_ship_fee_text.setVisibility(View.GONE);
                ship_text.setText("滿490免運");
            } else {
                shippingPrice = 60;
                no_ship_fee_text.setVisibility(View.VISIBLE);
                ship_text.setText("運費");
            }
        }
        shippingPriceText.setText("$" + Integer.toString(shippingPrice));
        totalPriceText.setText("$" + Integer.toString(totalGoodsPrice + shippingPrice));
    }

    public void updateLayoutByLoginStatus() {
        if (!Settings.checkIsLogIn()) {
            noLoginLayout.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.GONE);
        } else {
            noLoginLayout.setVisibility(View.GONE);
            confirmButton.setVisibility(View.VISIBLE);
        }
    }

    public void updateRecycleView() {
        ViewGroup.LayoutParams params = newsRecylerView.getLayoutParams();
        params.height = (int) DensityApi.convertDpToPixel(100 * shoppingCarProducts.size(), getActivity());
        newsRecylerView.setLayoutParams(params);

        ((ShoppingCarGoodsAdapter) newsRecylerView.getAdapter())
                .updateProductList(shoppingCarProducts);

        if (shoppingCarProducts.size() == 0) {
            noItemLayout.setVisibility(View.VISIBLE);
            ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
            activity.setBreadCurmbsVisibility(View.INVISIBLE);
        }
    }

    private void findView(View view) {
        newsRecylerView = (RecyclerView) view.findViewById(R.id.recycler_buy_goods);
        deliverButton = (Button) view.findViewById(R.id.deliver_button);
        totalGoodsPriceText = (TextView) view.findViewById(R.id.fragment1_goodsTotalPriceText);
        shippingPriceText = (TextView) view.findViewById(R.id.fragment1_shippingPriceText);
        totalPriceText = (TextView) view.findViewById(R.id.fragment1_totalPriceText);
        confirmButton = (Button) view.findViewById(R.id.fragment1_confirm_button);
        noItemLayout = (LinearLayout) view.findViewById(R.id.shopping_car_no_item_layout);
        noLoginLayout = (LinearLayout) view.findViewById(R.id.layout_buy_button);
        fb_buy_button = (Button) view.findViewById(R.id.fragment1_fb_buy_button);
        no_name_buy_button = (Button) view.findViewById(R.id.fragment1_no_name_buy_button);
        no_ship_fee_text = (TextView) view.findViewById(R.id.no_ship_fee_text);
        ship_text = (TextView) view.findViewById(R.id.ship_text);
    }

    private void initVisibleLayout() {
        ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
        if (shoppingCarProducts.size() == 0) {
            noItemLayout.setVisibility(View.VISIBLE);
            activity.setBreadCurmbsVisibility(View.INVISIBLE);
        } else {
            noItemLayout.setVisibility(View.GONE);
            activity.setBreadCurmbsVisibility(View.VISIBLE);
        }
    }

    private void setRecyclerView() {
        newsRecylerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newsRecylerView.setLayoutManager(mLayoutManager);
        ViewGroup.LayoutParams params = newsRecylerView.getLayoutParams();
        params.height = (int) DensityApi.convertDpToPixel(100 * shoppingCarProducts.size(), getActivity());
        newsRecylerView.setLayoutParams(params);
        ShoppingCarGoodsAdapter adapter = new ShoppingCarGoodsAdapter(getActivity(), shoppingCarProducts, this);
        newsRecylerView.setAdapter(adapter);
    }

    private void setNoNameBuyButton() {
        no_name_buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.ANONYMOUS_PURCHASE));
                startNextStep();
            }
        });
    }

    private void setConfirmButton() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.CONFIRM_FACEBOOK_LOGIN));
                startNextStep();
            }
        });
    }

    private void setFbBuyButton() {
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
                    activity.getOrder().setTotalPrice(shippingPrice + totalGoodsPrice);
                    activity.performClickFbButton();

                    GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.FACEBOOK_LOGIN));
                } else {
                    Toast.makeText(getActivity(), "請選擇運送方式", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDeliverButton() {
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("選擇運送方式");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("7-11超商取貨付款 $60");

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
                        }
                        updatePricesText();
                    }
                });

        deliverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builderSingle.show();
            }
        });
    }

    private void startNextStep() {
        if (shippingType != -1) {
            // save to activity order
            ShoppingCarActivity activity = (ShoppingCarActivity) getActivity();
            activity.saveOrderProducts(shoppingCarProducts);
            activity.getOrder().setShipPrice(shippingPrice);
            activity.getOrder().setProductPrice(totalGoodsPrice);
            activity.getOrder().setTotalPrice(shippingPrice + totalGoodsPrice);
            activity.startPurchaseFragment2();
        } else {
            Toast.makeText(getActivity(), "請選擇運送方式", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteButtonClick(int position) {
        GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.PRODUCT_DELETE));
        ShoppingCarPreference prefs = new ShoppingCarPreference();
        prefs.removeShoppingItem(getContext(), position);

        loadShoppingCart();
        updateRecycleView();
        updatePricesText();
    }

    @Override
    public void onSelectCountButtonClick(int position, int tempCount) {
        GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.NUMBER_CHANGE));
        this.tempCount = tempCount;
        View dialogView = getDialogView();
        getAlertDialog(position, dialogView).show();
    }

    private View getDialogView() {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_select_item_counts, null, false);
        final TextView countText = (TextView) view.findViewById(R.id.count_text_view);
        countText.setText(String.valueOf(tempCount));
        setMinusButton(view, countText);
        setPlusButton(view, countText);
        return view;
    }

    private void setMinusButton(View view, final TextView countText) {
        Button minusButton = (Button) view.findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempCount != 1) {
                    tempCount = tempCount - 1;
                    countText.setText(String.valueOf(tempCount));
                }
            }
        });
    }

    private void setPlusButton(View view, final TextView countText) {
        Button plusButton = (Button) view.findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                countText.setText(String.valueOf(tempCount));
            }
        });
    }

    private AlertDialog getAlertDialog(final int product_position, View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder.setTitle("選擇商品數量");
        alertDialogBuilder.setView(view);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                shoppingCarProducts.get(product_position).setBuy_count(tempCount);

                ShoppingCarPreference pref = new ShoppingCarPreference();
                for (int i = 0; i < shoppingCarProducts.size(); i++) {
                    pref.removeShoppingItem(getContext(), shoppingCarProducts.size() - 1 - i);
                }
                for (int i = 0; i < shoppingCarProducts.size(); i++) {
                    pref.addShoppingItem(getContext(), shoppingCarProducts.get(i));
                }
                loadShoppingCart();
                updateRecycleView();
                updatePricesText();
            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();
            }
        });

        return alertDialogBuilder.create();
    }
}
