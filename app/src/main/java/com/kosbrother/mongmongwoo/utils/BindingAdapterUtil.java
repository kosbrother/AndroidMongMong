package com.kosbrother.mongmongwoo.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;

public class BindingAdapterUtil {

    @BindingAdapter("bind:imageUrl")
    public static void loadImageUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .fitCenter()
                .placeholder(R.mipmap.img_pre_load_square)
                .into(imageView);
    }
}
