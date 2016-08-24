package com.kosbrother.mongmongwoo.category;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.model.Category;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private Context context;

    public SubCategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_sub_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (position >= categories.size()) {
            holder.itemView.setOnClickListener(null);
            holder.subCategoryImageView.setImageResource(0);
            holder.subCategoryNameTextView.setText("");
        } else {
            final Category category = categories.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int categoryId = category.getId();
                    String categoryName = category.getName();
                    Intent intent = new Intent(context, CategoryActivity.class);
                    intent.putExtra(CategoryActivity.EXTRA_INT_CATEGORY_ID, categoryId);
                    intent.putExtra(CategoryActivity.EXTRA_STRING_CATEGORY_NAME, categoryName);
                    context.startActivity(intent);
                }
            });
            Glide.with(context)
                    .load(category.getImageUrl())
                    .centerCrop()
                    .placeholder(R.mipmap.img_pre_load_square)
                    .into(holder.subCategoryImageView);
            holder.subCategoryNameTextView.setText(category.getName());
        }
    }

    @Override
    public int getItemCount() {
        // If size % 3 != 0, add empty view to fill recyclerView
        int size = categories.size();
        if (size % 3 == 0) {
            return size;
        }
        return size + (3 - size % 3);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView subCategoryImageView;
        public final TextView subCategoryNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            subCategoryImageView = (ImageView) itemView.findViewById(R.id.item_sub_category_iv);
            subCategoryNameTextView = (TextView) itemView.findViewById(R.id.item_sub_category_name_tv);
        }
    }
}
