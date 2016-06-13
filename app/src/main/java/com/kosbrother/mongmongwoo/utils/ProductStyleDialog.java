package com.kosbrother.mongmongwoo.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCarPreference;
import com.kosbrother.mongmongwoo.adpters.StyleGridAdapter;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.product.ProductSelectDialogConfirmEvent;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Spec;

public class ProductStyleDialog {

    private final Context context;
    private Product product;

    private final AlertDialog alertDialog;
    private final ImageView styleImage;
    private final TextView styleName;
    private final GridView styleGridView;
    private final TextView countTextView;

    private int tempCount = 1;

    @SuppressLint("InflateParams")
    public ProductStyleDialog(Context context,
                              Product product,
                              final ProductStyleDialogListener listener) {
        this.context = context;
        this.product = product;

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_add_shopping_car_item, null, false);
        styleName = (TextView) view.findViewById(R.id.dialog_style_name);
        styleImage = (ImageView) view.findViewById(R.id.dialog_style_image);
        styleGridView = (GridView) view.findViewById(R.id.dialog_styles_gridview);
        countTextView = (TextView) view.findViewById(R.id.count_text_view);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();

        initMinusButton(view);
        initPlusButton(view);
        initConfirmButton(view, listener);
        initGridView();
    }

    public void showWithInitState() {
        initStyleName();
        initStyleImage();
        initCountTextView();
        updateSelectedStyle(0);
        alertDialog.show();
    }

    public void showWithInitState(Product product) {
        this.product = product;
        initStyleName();
        initStyleImage();
        initCountTextView();
        initGridView();
        alertDialog.show();
    }

    private void initStyleName() {
        updateStyleName(product.getSpecs().get(0).getStyle());
    }

    private void initStyleImage() {
        updateStyleImage(product.getSpecs().get(0).getStylePic().getUrl());
    }

    private void initGridView() {
        StyleGridAdapter styleGridAdapter = new StyleGridAdapter(context, product.getSpecs());
        styleGridView.setAdapter(styleGridAdapter);
        styleGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateSelectedStyle(position);

                Spec spec = product.getSpecs().get(position);
                updateStyleName(spec.getStyle());
                updateStyleImage(spec.getStylePic().getUrl());
            }
        });
    }

    private void initCountTextView() {
        tempCount = 1;
        updateCountTextView();
    }

    private void initMinusButton(View view) {
        Button minusButton = (Button) view.findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempCount != 1) {
                    tempCount = tempCount - 1;
                    updateCountTextView();
                }
            }
        });
    }

    private void initPlusButton(View view) {
        Button plusButton = (Button) view.findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                updateCountTextView();
            }
        });
    }

    private void initConfirmButton(View view, final ProductStyleDialogListener listener) {
        Button styleConfirmButton = (Button) view.findViewById(R.id.dialog_style_confirm_button);
        styleConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new ProductSelectDialogConfirmEvent(product.getName()));
                updateConfirmProduct();
                saveProduct();
                checkFirstAddAndNotifyListener(listener);
                alertDialog.dismiss();
            }
        });
    }

    private void updateSelectedStyle(int position) {
        StyleGridAdapter adapter = (StyleGridAdapter) styleGridView.getAdapter();
        adapter.updateSelectedPosition(position);
    }

    private void updateCountTextView() {
        countTextView.setText(String.valueOf(tempCount));
    }

    private void updateStyleImage(String picUrl) {
        Glide.with(context)
                .load(picUrl)
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_square)
                .into(styleImage);
    }

    private void updateStyleName(String styleNameString) {
        styleName.setText(styleNameString);
    }

    private void updateConfirmProduct() {
        StyleGridAdapter adapter = (StyleGridAdapter) styleGridView.getAdapter();
        Spec selectedSpec = product.getSpecs().get(adapter.getSelectedPosition());
        product.setSelectedSpec(selectedSpec);
        product.setBuy_count(tempCount);
    }

    private void saveProduct() {
        ShoppingCarPreference pref = new ShoppingCarPreference();
        pref.addShoppingItem(context, product);
    }

    private void checkFirstAddAndNotifyListener(ProductStyleDialogListener listener) {
        if (Settings.checkIsFirstAddShoppingCar()) {
            Settings.setKownShoppingCar();
            listener.onFirstAddShoppingCart();
        }
        listener.onConfirmButtonClick();
    }

    public interface ProductStyleDialogListener {

        void onFirstAddShoppingCart();

        void onConfirmButtonClick();
    }
}
