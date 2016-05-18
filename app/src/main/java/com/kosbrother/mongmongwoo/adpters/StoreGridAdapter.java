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
import com.kosbrother.mongmongwoo.model.Store;

import java.util.List;

public class StoreGridAdapter extends BaseAdapter {

    private final Context context;
    private final List<Store> storeList;
    private int selectedStorePosition = -1;

    public StoreGridAdapter(Context context, List<Store> storeList) {
        this.context = context;
        this.storeList = storeList;
    }

    @Override
    public int getCount() {
        return storeList.size();
    }

    @Override
    public Object getItem(int position) {
        return storeList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_store_grid, null);

            viewHolder = new ViewHolder();
            viewHolder.storeText = (TextView) convertView.findViewById(R.id.store_text);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.storeText.setText(storeList.get(position).getName());

        if (position == selectedStorePosition) {
            viewHolder.storeText.setBackgroundResource(R.drawable.button_yellow_selector);
            viewHolder.storeText.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            viewHolder.storeText.setBackgroundResource(R.drawable.button_yellow_round_selector);
            viewHolder.storeText.setTextColor(ContextCompat.getColor(context, R.color.movie_indicator));
        }

        return convertView;
    }

    public void updateSelectedPosition(int position) {
        selectedStorePosition = position;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView storeText;
    }
}
