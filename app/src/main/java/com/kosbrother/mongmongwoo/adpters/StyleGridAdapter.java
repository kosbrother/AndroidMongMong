package com.kosbrother.mongmongwoo.adpters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.ProductSpec;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/23/16.
 */
public class StyleGridAdapter extends BaseAdapter {

    private Activity mActivity;
    private ArrayList<ProductSpec> specs;
    private ImageView imageView;
    private TextView nameTextView;
    private int selectedPosition;

    public StyleGridAdapter(Activity activity, ArrayList<ProductSpec> specs, ImageView imageView, TextView nameTextView) {
        mActivity = activity;
        this.specs = specs;
        this.imageView = imageView;
        this.nameTextView = nameTextView;
        selectedPosition = 0;
    }

    @Override
    public int getCount() {
        return specs.size();
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_style_grid, null);
        }

        TextView storeText = (TextView) convertView.findViewById(R.id.store_text);
        storeText.setText(specs.get(position).getStyle());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(mActivity)
                        .load(specs.get(position).getPic_url())
                        .centerCrop()
                        .placeholder(R.mipmap.img_pre_load_square)
                        .into(imageView);
                nameTextView.setText(specs.get(position).getStyle());
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });

        if (position == selectedPosition) {
            storeText.setBackgroundResource(R.drawable.round_select_style);
            storeText.setTextColor(mActivity.getResources().getColor(R.color.white));
        } else {
            storeText.setBackgroundResource(R.drawable.round_non_select_style);
            storeText.setTextColor(mActivity.getResources().getColor(R.color.gray_background));
        }

        return convertView;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}
