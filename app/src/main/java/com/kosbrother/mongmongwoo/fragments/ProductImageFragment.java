package com.kosbrother.mongmongwoo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.ZoomImageActivity;
import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.R;

public class ProductImageFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PIC = "THE_PIC_URL";

    String pic_url;

    public static ProductImageFragment newInstance(String pic_url) {
        Bundle args = new Bundle();
        args.putString(ARG_PIC, pic_url);
        ProductImageFragment fragment = new ProductImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pic_url = getArguments().getString(ARG_PIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ImageView view = (ImageView) inflater.inflate(R.layout.fragment_product_image, container, false);
        view.setOnClickListener(this);
        Glide.with(getActivity())
                .load(pic_url)
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load)
                .crossFade()
                .into(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        ProductActivity activity = (ProductActivity) getActivity();
        Intent intent = new Intent(activity, ZoomImageActivity.class);
        intent.putStringArrayListExtra(ZoomImageActivity.STRING_ARRAY_LIST_EXTRA_URL,
                activity.getImages());
        startActivity(intent);
    }

}
