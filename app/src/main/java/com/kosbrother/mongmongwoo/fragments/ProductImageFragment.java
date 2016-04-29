package com.kosbrother.mongmongwoo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.ZoomImageActivity;

public class ProductImageFragment extends Fragment implements View.OnClickListener {

    public static final String ARG_PIC = "THE_PIC_URL";
    public static final String ARG_INDEX = "ARG_INDEX";

    private String picUrl;
    private int picIndex;

    public static ProductImageFragment newInstance(String picUrl, int picIndex) {
        Bundle args = new Bundle();
        args.putString(ARG_PIC, picUrl);
        args.putInt(ARG_INDEX, picIndex);
        ProductImageFragment fragment = new ProductImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        picUrl = getArguments().getString(ARG_PIC);
        picIndex = getArguments().getInt(ARG_INDEX);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ImageView view = (ImageView) inflater.inflate(R.layout.fragment_product_image, container, false);
        view.setOnClickListener(this);
        Glide.with(getActivity())
                .load(picUrl)
                .centerCrop()
                .placeholder(R.mipmap.img_pre_load_rectangle)
                .into(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        ProductActivity activity = (ProductActivity) getActivity();
        Intent intent = new Intent(activity, ZoomImageActivity.class);
        intent.putStringArrayListExtra(ZoomImageActivity.STRING_ARRAY_LIST_EXTRA_URL,
                activity.getImages());
        intent.putExtra(ZoomImageActivity.INT_EXTRA_INDEX, picIndex);
        startActivity(intent);
    }

}
