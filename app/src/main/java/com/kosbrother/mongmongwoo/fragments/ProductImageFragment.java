package com.kosbrother.mongmongwoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;

/**
 * Created by kolichung on 3/17/16.
 */
public class ProductImageFragment extends Fragment {

    public static final String ARG_PIC = "THE_PIC_URL";
    String pic_url;

    public static ProductImageFragment newInstance(String pic_url) {

        Bundle args = new Bundle();
        args.putString(ARG_PIC,pic_url);
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
        View view = inflater.inflate(R.layout.fragment_product_image, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.product_image_view);
        Glide.with(getActivity())
                .load(pic_url)
                .centerCrop()
                .placeholder(R.drawable.icon_head)
                .crossFade()
                .into(imageView);

        return view;
    }
}
