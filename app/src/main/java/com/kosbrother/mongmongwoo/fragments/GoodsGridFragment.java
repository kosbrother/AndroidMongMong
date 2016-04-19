package com.kosbrother.mongmongwoo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.GoodsGridAdapter;
import com.kosbrother.mongmongwoo.api.WebService;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.EndlessScrollListener;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * Created by kolichung on 3/1/16.
 */
public class GoodsGridFragment extends Fragment implements Action1<ArrayList<Product>> {

    private GridView mGridView;
    private GoodsGridAdapter goodsGridAdapter;
    private RelativeLayout layoutProgress;

    private ArrayList<Product> products = new ArrayList<>();
    private int category_id;
    public static final String ARG_CATEGORY = "CATEGORY_ID";

    private int page = 1;

    public static GoodsGridFragment newInstance(int category_id) {
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY, category_id);
        GoodsGridFragment fragment = new GoodsGridFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        category_id = getArguments().getInt(ARG_CATEGORY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        mGridView = (GridView) view.findViewById(R.id.fragment_gridview);
        layoutProgress = (RelativeLayout) view.findViewById(R.id.layout_progress);

        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getCategoryProducts();
            }
        });

        if (goodsGridAdapter != null) {
            layoutProgress.setVisibility(View.GONE);
            mGridView.setAdapter(goodsGridAdapter);
        } else {
            layoutProgress.setVisibility(View.VISIBLE);
            getCategoryProducts();
        }
        return view;
    }

    public void notifyCategoryChanged(int category_id) {

        this.category_id = category_id;
        products.clear();
        layoutProgress.setVisibility(View.VISIBLE);
        page = 1;
        getCategoryProducts();

        try {
            MainActivity activity = (MainActivity) getActivity();
            activity.sendFragmentCategoryName(category_id);
        } catch (Exception e) {

        }
    }

    public int getProductsSize() {
        if (products != null) {
            return products.size();
        } else {
            return 0;
        }
    }

    private void getCategoryProducts() {
        WebService.getCategoryProducts(category_id, page, this);
    }

    @Override
    public void call(ArrayList<Product> newProducts) {
        if (newProducts != null && newProducts.size() > 0) {
            products.addAll(newProducts);
            page = page + 1;
            if (goodsGridAdapter == null) {
                MainActivity activity = (MainActivity) getActivity();
                goodsGridAdapter = new GoodsGridAdapter(activity, products);
                mGridView.setAdapter(goodsGridAdapter);
            } else {
                goodsGridAdapter.notifyDataSetChanged();
            }
        } else {
            // TODO: 2016/4/19 error handle
        }
        layoutProgress.setVisibility(View.GONE);
    }
}
