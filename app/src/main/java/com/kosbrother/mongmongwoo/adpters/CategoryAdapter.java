package com.kosbrother.mongmongwoo.adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.Category;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<Category> mCategories;

    public CategoryAdapter(Context context, List<Category> categories) {
        mContext = context;
        mCategories = categories;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_category, null);
            viewHolder = new ViewHolder();
            viewHolder.categoryImageView = (ImageView) convertView.findViewById(R.id.category_iv);
            viewHolder.categoryNameTextView = (TextView) convertView.findViewById(R.id.category_name_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Category category = mCategories.get(position);
        Glide.with(mContext)
                .load(category.getImageUrl())
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_square)
                .into(viewHolder.categoryImageView);
        viewHolder.categoryNameTextView.setText(category.getName());
        return convertView;
    }

    private static class ViewHolder {
        private ImageView categoryImageView;
        private TextView categoryNameTextView;
    }

}
