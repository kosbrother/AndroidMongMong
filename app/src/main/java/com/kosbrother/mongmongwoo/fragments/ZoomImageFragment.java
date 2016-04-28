package com.kosbrother.mongmongwoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.utils.TouchImageView;

public class ZoomImageFragment extends Fragment {
    public static final String ARG_PIC = "THE_PIC_URL";

    String pic_url;

    public static ZoomImageFragment newInstance(String pic_url) {

        Bundle args = new Bundle();
        args.putString(ARG_PIC, pic_url);
        ZoomImageFragment fragment = new ZoomImageFragment();
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
        TouchImageView imageView = (TouchImageView)
                inflater.inflate(R.layout.fragment_zoom_in_out_image, container, false);

        Glide.with(getActivity())
                .load(pic_url)
                .placeholder(R.drawable.icon_head)
                .fitCenter()
                .into(imageView);

        return imageView;
    }

}
