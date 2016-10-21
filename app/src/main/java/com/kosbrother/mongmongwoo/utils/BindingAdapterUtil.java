package com.kosbrother.mongmongwoo.utils;

import android.databinding.BindingAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;

public class BindingAdapterUtil {

    @BindingAdapter("bind:squareImageUrl")
    public static void loadSquareImageUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .fitCenter()
                .placeholder(R.mipmap.img_pre_load_square)
                .into(imageView);
    }

    @BindingAdapter("bind:rectImageUrl")
    public static void loadRectImageUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .fitCenter()
                .placeholder(R.mipmap.img_pre_load_rectangle)
                .into(imageView);
    }

    @BindingAdapter("bind:bannerImageUrl")
    public static void loadBannerImageUrl(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .fitCenter()
                .placeholder(R.mipmap.banner_default)
                .into(imageView);
    }

    @BindingAdapter("bind:imageUrlWithoutPlaceholder")
    public static void loadImageUrlWithoutPlaceholder(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .fitCenter()
                .into(imageView);
    }

    @BindingAdapter("bind:paintLineThrough")
    public static void paintLineThrough(TextView textView, boolean paintLineThrough) {
        if (paintLineThrough) {
            TextViewUtil.paintLineThroughTextView(textView);
        } else {
            TextViewUtil.removeLineThroughTextView(textView);
        }
    }
}
