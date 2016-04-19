package com.kosbrother.mongmongwoo.adpters;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.ShoppingCarPreference;
import com.kosbrother.mongmongwoo.api.WebService;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.ProductSpec;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * Created by kolichung on 3/1/16.
 */
public class GoodsGridAdapter extends BaseAdapter implements Action1<ArrayList<ProductSpec>> {

    private MainActivity mActivity;
    private ArrayList<Product> products;
    private ArrayList<ProductSpec> specs;
    private int theTaskProductId;

    private int selectedProductPosition;
    private GridView styleGrid;
    private ImageView styleImage;
    private TextView styleName;
    private int tempCount;

    private StyleGridAdapter styleGridAdapter;

    public GoodsGridAdapter(MainActivity activity, ArrayList<Product> products) {
        mActivity = activity;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // If convertView is null then inflate the appropriate layout file
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_goods_2, null);
        }
        ImageView itemImage = (ImageView) convertView.findViewById(R.id.item_imageview);
        TextView itemNameText = (TextView) convertView.findViewById(R.id.item_name_text);
        TextView itemPriceText = (TextView) convertView.findViewById(R.id.item_price_text);
        Button addShoppingCarButton = (Button) convertView.findViewById(R.id.add_shopping_car_button);

        final Product theProduct = products.get(position);

        Glide.with(mActivity)
                .load(theProduct.getPic_url())
                .centerCrop()
                .placeholder(R.drawable.icon_head)
                .crossFade()
                .into(itemImage);
        itemNameText.setText(theProduct.getName());
        itemPriceText.setText("NT$" + Integer.toString(theProduct.getPrice()));

        addShoppingCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ShoppingCarPreference pref = new ShoppingCarPreference();
//                theProduct.setBuy_count(1);
//                pref.addShoppingItem(mActivity, theProduct);
//                mActivity.doIncrease();
                theTaskProductId = theProduct.getId();
                selectedProductPosition = position;
                if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
                    showStyleDialog();
                    WebService.getProductSpecs(theTaskProductId, GoodsGridAdapter.this);
                } else {
                    Toast.makeText(mActivity, "無法取得資料,請檢查網路連線", Toast.LENGTH_SHORT).show();
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ProductActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Selected_Product", theProduct);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });

        return convertView;
    }


    public void showStyleDialog() {

        View view = LayoutInflater.from(mActivity)
                .inflate(R.layout.dialog_add_shopping_car_item, null, false);
        styleGrid = (GridView) view.findViewById(R.id.dialog_styles_gridview);
        styleImage = (ImageView) view.findViewById(R.id.dialog_style_image);
        styleName = (TextView) view.findViewById(R.id.dialog_style_name);

        Button style_confirm_button = (Button) view.findViewById(R.id.dialog_style_confirm_button);

        Button minusButton = (Button) view.findViewById(R.id.minus_button);
        Button plusButton = (Button) view.findViewById(R.id.plus_button);
        final TextView countText = (TextView) view.findViewById(R.id.count_text_view);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tempCount != 1) {
                    tempCount = tempCount - 1;
                    countText.setText(Integer.toString(tempCount));
                }
            }
        });
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempCount = tempCount + 1;
                countText.setText(Integer.toString(tempCount));
            }
        });
        tempCount = 1;
        countText.setText(Integer.toString(tempCount));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setView(view);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();

        style_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedStylePosition = styleGridAdapter.getSelectedPosition();
                ProductSpec theSelectedSpec = specs.get(selectedStylePosition);
                Product theProduct = products.get(selectedProductPosition);
                theProduct.setSelectedSpec(theSelectedSpec);
                ShoppingCarPreference pref = new ShoppingCarPreference();
                theProduct.setBuy_count(tempCount);
                pref.addShoppingItem(mActivity, theProduct);
                mActivity.doIncrease();
                alertDialog.cancel();

                if (Settings.checkIsFirstAddShoppingCar(mActivity)) {
                    mActivity.showShoppingCarInstruction();
                    Settings.setKownShoppingCar(mActivity);
                }

            }
        });

    }

    @Override
    public void call(ArrayList<ProductSpec> productSpecs) {
        specs = productSpecs;
        if (specs != null && specs.size() != 0) {
            styleGridAdapter = new StyleGridAdapter(mActivity, specs, styleImage, styleName);
            styleGrid.setAdapter(styleGridAdapter);

            Glide.with(mActivity)
                    .load(specs.get(0).getPic_url())
                    .centerCrop()
                    .placeholder(R.drawable.icon_head)
                    .crossFade()
                    .into(styleImage);
            styleName.setText(specs.get(0).getStyle());
        } else {
            Toast.makeText(mActivity, "無法取得資料,請檢查網路連線", Toast.LENGTH_SHORT).show();
        }
    }

}
