package com.kosbrother.mongmongwoo.checkout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep1ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep1EnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCartManager;
import com.kosbrother.mongmongwoo.utils.CalculateUtil;
import com.kosbrother.mongmongwoo.utils.ProductStyleDialog;

import java.util.List;

public class PurchaseFragment1 extends Fragment {

    public static final int SHIP_FEE = 90;
    private static final int FREE_SHIP_REQUIRED_PRICE = 490;

    private LinearLayout noLoginLayout;
    private Button loginButton;
    private Button guestCheckoutButton;

    private TextView totalGoodsPriceText;
    private TextView shippingPriceText;
    private TextView totalPriceText;
    private Button confirmButton;
    private TextView freeShippingPriceRemainTextView;
    private TextView checkoutPriceBottomTextView;
    private LinearLayout goodsContainerLinearLayout;

    private List<Product> shoppingCarProducts;
    private int totalGoodsPrice;
    private int shippingPrice;

    private int tempCount;
    private OnStep1ButtonClickListener mCallback;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shoppingCarProducts = ((ShoppingCarActivity) getActivity()).getProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (shoppingCarProducts == null || shoppingCarProducts.size() == 0) {
            return inflater.inflate(R.layout.view_stub_empty_shopping_car, container, false);
        }

        View view = inflater.inflate(R.layout.fragment_purchase1, container, false);
        findView(view);

        addGoodsViewToLinearLayout();
        setLoginButton();
        setGuestCheckoutButton();
        setConfirmButton();

        updatePricesText();
        updateLayoutByLoginStatus();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        GAManager.sendEvent(new CheckoutStep1EnterEvent());
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

    private void updatePricesText() {
        totalGoodsPrice = CalculateUtil.calculateTotalGoodsPrice(shoppingCarProducts);
        shippingPrice = totalGoodsPrice >= FREE_SHIP_REQUIRED_PRICE ? 0 : SHIP_FEE;

        String totalGoodsPriceString = "$" + totalGoodsPrice;
        totalGoodsPriceText.setText(totalGoodsPriceString);

        String shippingPriceString;
        String freeShippingPriceRemainString;
        if (shippingPrice == 0) {
            shippingPriceString = "免運費";
            freeShippingPriceRemainString = "NT$ " + 0;
        } else {
            shippingPriceString = "$" + shippingPrice;
            freeShippingPriceRemainString = "NT$ " + (FREE_SHIP_REQUIRED_PRICE - totalGoodsPrice);
        }
        freeShippingPriceRemainTextView.setText(freeShippingPriceRemainString);
        shippingPriceText.setText(shippingPriceString);

        String totalPrice = "$" + (totalGoodsPrice + shippingPrice);
        totalPriceText.setText(totalPrice);
        checkoutPriceBottomTextView.setText(totalPrice);
    }

    private void updateGoodsLinearLayout() {
        goodsContainerLinearLayout.removeAllViews();
        addGoodsViewToLinearLayout();
    }

    private void findView(View view) {
        totalGoodsPriceText = (TextView) view.findViewById(R.id.fragment1_goodsTotalPriceText);
        shippingPriceText = (TextView) view.findViewById(R.id.fragment1_shippingPriceText);
        totalPriceText = (TextView) view.findViewById(R.id.fragment1_totalPriceText);
        confirmButton = (Button) view.findViewById(R.id.fragment1_confirm_button);
        noLoginLayout = (LinearLayout) view.findViewById(R.id.layout_buy_button);
        loginButton = (Button) view.findViewById(R.id.login_btn);
        guestCheckoutButton = (Button) view.findViewById(R.id.fragment1_guest_checkout_btn);
        freeShippingPriceRemainTextView = (TextView) view.findViewById(R.id.free_shipping_price_remain_tv);
        checkoutPriceBottomTextView = (TextView) view.findViewById(R.id.checkout_price_bottom_tv);
        goodsContainerLinearLayout = (LinearLayout) view.findViewById(R.id.goods_container_ll);
    }

    @SuppressLint("InflateParams")
    private void addGoodsViewToLinearLayout() {
        for (int i = 0; i < shoppingCarProducts.size(); i++) {
            final Product product = shoppingCarProducts.get(i);
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

            String priceString = "NT$ " + product.getFinalPrice() + "X " + product.getBuy_count();
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

            String subTotalText = "小計：NT$ " + (product.getBuy_count() * product.getFinalPrice());
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
                GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.ANONYMOUS_PURCHASE));
                mCallback.onGuestCheckoutClick(totalGoodsPrice, shippingPrice);
            }
        });
    }

    private void setLoginButton() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.LOGIN));
                mCallback.onLoginClick(totalGoodsPrice, shippingPrice);
            }
        });
    }

    private void setConfirmButton() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CheckoutStep1ClickEvent(GALabel.CONFIRM_FACEBOOK_LOGIN));
                mCallback.onConfirmButtonClick(totalGoodsPrice, shippingPrice);
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

                shoppingCarProducts.remove(position);
                ShoppingCartManager.getInstance().removeShoppingItem(position);
                if (shoppingCarProducts.size() == 0) {
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
                shoppingCarProducts.get(productPosition).setSelectedSpec(product.getSelectedSpec());
                ShoppingCartManager.getInstance().storeShoppingItems(shoppingCarProducts);
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
                shoppingCarProducts.get(product_position).setBuy_count(tempCount);
                ShoppingCartManager.getInstance().storeShoppingItems(shoppingCarProducts);
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

    public interface OnStep1ButtonClickListener {

        void onGuestCheckoutClick(int totalGoodsPrice, int shippingPrice);

        void onLoginClick(int totalGoodsPrice, int shippingPrice);

        void onConfirmButtonClick(int totalGoodsPrice, int shippingPrice);

        void onNoShoppingItem();
    }
}
