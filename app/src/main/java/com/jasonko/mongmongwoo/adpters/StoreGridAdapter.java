package com.jasonko.mongmongwoo.adpters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jasonko.mongmongwoo.R;
import com.jasonko.mongmongwoo.SelectDeliverStoreActivity;
import com.jasonko.mongmongwoo.model.Store;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/14/16.
 */
public class StoreGridAdapter extends BaseAdapter {

    private SelectDeliverStoreActivity mActivity;
    private ArrayList<Store> stores;

    public StoreGridAdapter(SelectDeliverStoreActivity activity, ArrayList<Store> stores) {
        mActivity = activity;
        this.stores = stores;
    }

    @Override
    public int getCount() {
        return stores.size();
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
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_store_grid, null);
        }

        TextView storeText = (TextView) convertView.findViewById(R.id.store_text);
        storeText.setText(stores.get(position).getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store theStore = stores.get(position);
                mActivity.setStoreInfo(theStore);
            }
        });

        return convertView;
    }
}
