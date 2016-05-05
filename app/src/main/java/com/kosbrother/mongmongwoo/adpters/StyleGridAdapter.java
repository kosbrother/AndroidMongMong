package com.kosbrother.mongmongwoo.adpters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.ProductSpec;

import java.util.ArrayList;

public class StyleGridAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<ProductSpec> specs;
    private int selectedPosition;

    public StyleGridAdapter(Context context, ArrayList<ProductSpec> specs) {
        this.context = context;
        this.specs = specs;
        selectedPosition = 0;
    }

    @Override
    public int getCount() {
        return specs.size();
    }

    @Override
    public Object getItem(int position) {
        return specs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_style_grid, null);

            viewHolder = new ViewHolder();
            viewHolder.storeText = (TextView) convertView.findViewById(R.id.store_text);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.storeText.setText(specs.get(position).getStyle());

        if (position == selectedPosition) {
            viewHolder.storeText.setBackgroundResource(R.drawable.round_select_style);
            viewHolder.storeText.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            viewHolder.storeText.setBackgroundResource(R.drawable.round_non_select_style);
            viewHolder.storeText.setTextColor(ContextCompat.getColor(context, R.color.gray_background));
        }

        return convertView;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void updateSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView storeText;
    }
}
