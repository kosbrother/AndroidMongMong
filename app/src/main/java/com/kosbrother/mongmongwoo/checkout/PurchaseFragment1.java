package com.kosbrother.mongmongwoo.checkout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.ShipInfoEntity;
import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsDetailEntity;
import com.kosbrother.mongmongwoo.facebookevent.FacebookLogger;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep1ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep1EnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.login.LoginActivity;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;
import com.kosbrother.mongmongwoo.utils.CalculateUtil;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;
import com.kosbrother.mongmongwoo.widget.CenterProgressDialog;

import java.util.List;

import rx.functions.Action1;

public class PurchaseFragment1 extends Fragment {

    public static final String SHOPPING_POINTS_TEXT = "折扣NT$ %s";
    private static final int REQUEST_LOGIN = 111;

    private LinearLayout noLoginLayout;
    private Button loginButton;
    private Button guestCheckoutButton;

    private View loadingView;
    private TextView totalGoodsPriceText;
    private TextView shippingPriceText;
    private Button confirmButton;
    private TextView freeShippingPriceRemainTextView;
    private TextView checkoutPriceBottomTextView;
    private LinearLayout goodsContainerLinearLayout;
    private View shoppingPointsView;
    private TextView shoppingPointsAmountTextView;
    private View shoppingPointsSubTotalView;
    private TextView shoppingPointsSubTotalTextView;
    private TextView deliveryTextView;
    private CenterProgressDialog progressDialog;

    private List<Product> products;

    private CalculateUtil.OrderPrice orderPrice;
    private int shoppingPointsAmount = 0;
    private int tempCount;
    private OnStep1ButtonClickListener mCallback;
    private AppCompatCheckBox shoppingPointsCheckBox;
    private DataManager.ApiCallBack getShoppingPointsCallBack;
    private String selectedDelivery = "請選擇";

    private String email = Settings.getEmail();
    private DataManager.CheckResultCallBack checkPickupRecordCallBack = new DataManager.CheckResultCallBack() {
        @Override
        public void onSuccessWithErrorMessage(String errorMessage) {
            progressDialog.dismiss();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setTitle("錯誤");
            alertDialogBuilder.setMessage(errorMessage);
            alertDialogBuilder.setPositiveButton("確認", null);
            alertDialogBuilder.show();
        }

        @Override
        public void onError(String errorMessage) {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(Object data) {
            progressDialog.dismiss();
            mCallback.onStep1NextButtonClick(email, orderPrice, selectedDelivery);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnStep1ButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnStep1ButtonClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        products = ((ShoppingCarActivity) getActivity()).getProducts();
        if (products == null || products.size() == 0) {
            return inflater.inflate(R.layout.view_stub_empty_shopping_car, container, false);
        }

        logInitiatedCheckoutEvent();
        View view = inflater.inflate(R.layout.fragment_purchase1, container, false);
        findView(view);

        addGoodsViewToLinearLayout();
        setDeliveryTextView();
        setLoginButton();
        setGuestCheckoutButton();
        setConfirmButton();

        updatePricesText();
        updateLayoutByLoginStatus(Settings.checkIsLogIn());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GAManager.sendEvent(new CheckoutStep1EnterEvent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                email = data.getStringExtra(LoginActivity.EXTRA_STRING_EMAIL);
                updateLayoutByLoginStatus(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        DataManager.getInstance().unSubscribe(getShoppingPointsCallBack);
        super.onDestroy();
    }

    public void updateLayoutByLoginStatus(final boolean login) {
        if (login) {
            loadingView.setVisibility(View.VISIBLE);
            noLoginLayout.setVisibility(View.GONE);
            confirmButton.setVisibility(View.VISIBLE);

            getShoppingPointsCallBack = new DataManager.ApiCallBack() {
                @Override
                public void onError(String errorMessage) {
                    loadingView.setVisibility(View.GONE);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Object data) {
                    ShoppingPointsDetailEntity entity = (ShoppingPointsDetailEntity) data;
                    int total = entity.getTotal();
                    if (total > 0) {
                        initShoppingPointView(total);
                    }
                    loadingView.setVisibility(View.GONE);
                }
            };
            int userId = Settings.getSavedUser().getUserId();
            DataManager.getInstance().getShoppingPointsInfo(userId, getShoppingPointsCallBack);
        } else {
            loadingView.setVisibility(View.GONE);
            noLoginLayout.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.GONE);
        }
    }

    private void initShoppingPointView(final int total) {
        String checkBoxText = String.format(SHOPPING_POINTS_TEXT, total);
        shoppingPointsCheckBox.setText(checkBoxText);

        shoppingPointsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                shoppingPointsAmount = checked ? total : 0;
                updatePricesText();
            }
        });
        shoppingPointsView.setVisibility(View.VISIBLE);
    }

    private void logInitiatedCheckoutEvent() {
        for (Product product : products) {
            FacebookLogger.getInstance().logInitiatedCheckoutEvent(
                    String.valueOf(product.getId()),
                    product.getCategoryName(),
                    product.getName(),
                    product.getBuy_count(),
                    true,
                    product.getFinalPrice() * product.getBuy_count()
            );
        }
    }

    private void updatePricesText() {
        orderPrice = CalculateUtil.calculateOrderPrice(products, shoppingPointsAmount);

        String totalGoodsPriceString = "NT$ " + orderPrice.getItemsPrice();
        totalGoodsPriceText.setText(totalGoodsPriceString);

        String freeShippingPriceRemainText = "NT$ " + orderPrice.getFreeShippingRemain();
        freeShippingPriceRemainTextView.setText(freeShippingPriceRemainText);

        String shoppingPriceText = "NT$ " + orderPrice.getShipFee();
        shippingPriceText.setText(shoppingPriceText);

        String totalPrice = "NT$ " + orderPrice.getTotal();
        checkoutPriceBottomTextView.setText(totalPrice);

        int shoppingPointsAmount = orderPrice.getShoppingPointsAmount();
        String shoppingPointText = "-NT$ " + shoppingPointsAmount;
        shoppingPointsAmountTextView.setText(shoppingPointText);

        shoppingPointsSubTotalView.setVisibility(shoppingPointsAmount > 0 ? View.VISIBLE : View.GONE);
        String shoppingPointsSubTotalText = "NT$ " + orderPrice.getShoppingPointsSubTotal();
        shoppingPointsSubTotalTextView.setText(shoppingPointsSubTotalText);
    }

    private void updateGoodsLinearLayout() {
        goodsContainerLinearLayout.removeAllViews();
        addGoodsViewToLinearLayout();
    }

    private void findView(View view) {
        loadingView = view.findViewById(R.id.loading_fragment_fl);
        totalGoodsPriceText = (TextView) view.findViewById(R.id.fragment1_goodsTotalPriceText);
        shippingPriceText = (TextView) view.findViewById(R.id.fragment1_shippingPriceText);
        confirmButton = (Button) view.findViewById(R.id.fragment1_confirm_button);
        noLoginLayout = (LinearLayout) view.findViewById(R.id.layout_buy_button);
        loginButton = (Button) view.findViewById(R.id.login_btn);
        guestCheckoutButton = (Button) view.findViewById(R.id.fragment1_guest_checkout_btn);
        freeShippingPriceRemainTextView = (TextView) view.findViewById(R.id.free_shipping_price_remain_tv);
        checkoutPriceBottomTextView = (TextView) view.findViewById(R.id.checkout_price_bottom_tv);
        goodsContainerLinearLayout = (LinearLayout) view.findViewById(R.id.goods_container_ll);

        shoppingPointsView = view.findViewById(R.id.fragment_purchase1_shopping_points_ll);
        shoppingPointsCheckBox = (AppCompatCheckBox) view.findViewById(R.id.fragment_purchase1_shopping_points_cb);
        shoppingPointsAmountTextView = (TextView) view.findViewById(R.id.fragment_purchase1_shopping_points_amount_tv);
        shoppingPointsSubTotalView = view.findViewById(R.id.fragment_purchase1_shopping_points_subTotal_ll);
        shoppingPointsSubTotalTextView = (TextView) view.findViewById(R.id.fragment_purchase1_shopping_points_subTotal_tv);
        deliveryTextView = (TextView) view.findViewById(R.id.fragment_purchase1_delivery_tv);
    }

    @SuppressLint("InflateParams")
    private void addGoodsViewToLinearLayout() {
        for (int i = 0; i < products.size(); i++) {
            final Product product = products.get(i);
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_buy_goods, null);
            goodsContainerLinearLayout.addView(itemView);

            TextView goodsNameTextView = (TextView) itemView.findViewById(R.id.item_car_name);
            TextView priceTextView = (TextView) itemView.findViewById(R.id.item_car_price);
            ImageView goodsImageView = (ImageView) itemView.findViewById(R.id.item_car_ig);
            ImageView deleteImageView = (ImageView) itemView.findViewById(R.id.item_car_delete_iv);
            Button styleButton = (Button) itemView.findViewById(R.id.item_car_style_btn);
            Button selectCountButton = (Button) itemView.findViewById(R.id.item_car_count_button);
            TextView subTotalTextView = (TextView) itemView.findViewById(R.id.subtotal_tv);
            Glide.with(getContext())
                    .load(product.getSelectedSpec().getStylePic().getUrl())
                    .centerCrop()
                    .placeholder(R.mipmap.img_pre_load_square)
                    .into(goodsImageView);

            String nameString = product.getName();
            goodsNameTextView.setText(nameString);

            String priceString = "NT$ " + product.getFinalPrice() + " X " + product.getBuy_count();
            priceTextView.setText(priceString);

            String styleText = product.getSelectedSpec().getStyle();
            styleButton.setText(styleText);
            styleButton.setTag(i);
            styleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelectStyleButtonClick((int) v.getTag(), product);
                }
            });

            String countText = "數量：" + product.getBuy_count();
            selectCountButton.setText(countText);
            selectCountButton.setTag(i);
            selectCountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelectCountButtonClick((int) v.getTag(), product.getBuy_count());
                }
            });

            String subTotalText = "小計：" + (product.getBuy_count() * product.getFinalPrice());
            subTotalTextView.setText(subTotalText);

            deleteImageView.setTag(i);
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteImageViewClick((int) v.getTag());
                }
            });

        }
    }

    private void setGuestCheckoutButton() {
        guestCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextStepButtonClick();
            }
        });
    }

    private void setDeliveryTextView() {
        deliveryTextView.setText(selectedDelivery);
        deliveryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DeliveryDialog(getContext(), deliveryTextView.getText().toString(),
                        new Action1<String>() {
                            @Override
                            public void call(String selectedDelivery) {
                                PurchaseFragment1.this.selectedDelivery = selectedDelivery;
                                deliveryTextView.setText(selectedDelivery);
                            }
                        }).show();
            }
        });
    }

    private void setLoginButton() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.LOGIN));
                startLoginActivity();
            }
        });
    }

    private void setConfirmButton() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextStepButtonClick();
            }
        });
    }

    public void onDeleteImageViewClick(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("刪除商品");
        alertDialogBuilder.setMessage("是否確定要刪除商品");
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.PRODUCT_DELETE));

                products.remove(position);
                ShoppingCartManager.getInstance().removeShoppingItem(position);
                if (products.size() == 0) {
                    mCallback.onNoShoppingItem();
                } else {
                    updateGoodsLinearLayout();
                    updatePricesText();
                }
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void onNextStepButtonClick() {
        if (selectedDelivery.equals("請選擇")) {
            Toast.makeText(getContext(), "請選擇取貨方式", Toast.LENGTH_SHORT).show();
            return;
        }

        if (orderPrice.getItemsPrice() < orderPrice.getShipFee()) {
            showPriceAlertDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startNextStep();
                }
            });
        } else {
            startNextStep();
        }
    }

    private void startNextStep() {
        if (Settings.checkIsLogIn()) {
            GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.CONFIRM_FACEBOOK_LOGIN));
            if (selectedDelivery.equals(getString(R.string.dialog_delivery_store))) {
                progressDialog = CenterProgressDialog.show(getActivity(), new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        DataManager.getInstance().unSubscribe(checkPickupRecordCallBack);
                    }
                });
                ShipInfoEntity shipInfoEntity = new ShipInfoEntity(
                        Settings.getSavedUser().getUserId(),
                        "",
                        "");
                DataManager.getInstance().checkPickupRecord(shipInfoEntity, checkPickupRecordCallBack);
            } else {
                mCallback.onStep1NextButtonClick(email, orderPrice, selectedDelivery);
            }
        } else {
            GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.ANONYMOUS_PURCHASE));
            mCallback.onStep1NextButtonClick(email, orderPrice, selectedDelivery);
        }
    }

    private void onSelectStyleButtonClick(final int productPosition, Product product) {
        List<Spec> specs = product.getSpecs();
        if (specs == null || specs.size() == 0) {
            Toast.makeText(getActivity(), "商品樣式讀取錯誤，如要更改樣式，請移除商品，重新加入購物車", Toast.LENGTH_SHORT).show();
            return;
        }
        ProductStyleDialog styleDialog = new ProductStyleDialog(getContext(), product, new ProductStyleDialog.ProductStyleDialogListener() {
            @Override
            public void onFirstAddShoppingCart() {

            }

            @Override
            public void onConfirmButtonClick(Product product) {
                products.get(productPosition).setSelectedSpec(product.getSelectedSpec());
                ShoppingCartManager.getInstance().storeShoppingItems(products);
                updateGoodsLinearLayout();
            }
        });
        styleDialog.showNoCountStyleDialog(product);
    }

    public void onSelectCountButtonClick(int position, int tempCount) {
        GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.NUMBER_CHANGE));
        this.tempCount = tempCount;
        View dialogView = getDialogView();
        getAlertDialog(position, dialogView).show();
    }

    @SuppressLint("InflateParams")
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
        alertDialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                products.get(product_position).setBuy_count(tempCount);
                ShoppingCartManager.getInstance().storeShoppingItems(products);
                updateGoodsLinearLayout();
                updatePricesText();
            }
        });
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return alertDialogBuilder.create();
    }

    private void showPriceAlertDialog(DialogInterface.OnClickListener onConfirmClickListener) {
        android.app.AlertDialog.Builder alertDialogBuilder =
                new android.app.AlertDialog.Builder(getContext());

        alertDialogBuilder
                .setTitle("購買金額低於運費")
                .setMessage(String.format("提醒您，您所購買的金額低於運費%s元，是否確認購買",
                        CalculateUtil.SHIP_FEE))
                .setCancelable(false)
                .setPositiveButton("確認", onConfirmClickListener)
                .setNegativeButton("再逛逛", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    public interface OnStep1ButtonClickListener {

        void onStep1NextButtonClick(String email, CalculateUtil.OrderPrice orderPrice, String delivery);

        void onNoShoppingItem();
    }
}
