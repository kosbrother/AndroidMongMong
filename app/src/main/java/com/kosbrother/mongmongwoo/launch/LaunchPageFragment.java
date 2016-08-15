package com.kosbrother.mongmongwoo.launch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kosbrother.mongmongwoo.R;

public class LaunchPageFragment extends Fragment {

    private static final String BUNDLE_INT_POSITION = "BUNDLE_INT_POSITION";
    private int position;

    public static Fragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt(BUNDLE_INT_POSITION, position);
        LaunchPageFragment fragment = new LaunchPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(BUNDLE_INT_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_launch_page, container, false);

        View root = rootView.findViewById(R.id.launch_page_root);
        root.setBackgroundResource(getBackgroundRes());

        Button startButton = (Button) rootView.findViewById(R.id.start_btn);
        startButton.setVisibility(lastPage() ? View.VISIBLE : View.GONE);
        return rootView;
    }

    private int getBackgroundRes() {
        int backgroundRes;
        switch (position) {
            case 0:
                backgroundRes = R.mipmap.img_landingpage_1;
                break;
            case 1:
                backgroundRes = R.mipmap.img_landingpage_2;
                break;
            case 2:
                backgroundRes = R.mipmap.img_landingpage_3;
                break;
            default:
                backgroundRes = R.mipmap.img_landingpage_1;
                break;
        }
        return backgroundRes;
    }

    private boolean lastPage() {
        return position == 2;
    }
}
