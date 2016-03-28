package com.jasonko.mongmongwoo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.jasonko.mongmongwoo.MainActivity;
import com.jasonko.mongmongwoo.R;
import com.jasonko.mongmongwoo.adpters.GoodsGridAdapter;
import com.jasonko.mongmongwoo.api.ProductApi;
import com.jasonko.mongmongwoo.model.Product;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/9/16.
 */
public class CategoryGoodsFragment extends Fragment {

    public static final String ARG_CATEGORY = "CATEGORY_ID";
    private GridView mGridView;
    private GoodsGridAdapter goodsGridAdapter;
    int category_id;

    ArrayList<Product> products;

    public static CategoryGoodsFragment newInstance(int category_id) {
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY, category_id);
        CategoryGoodsFragment fragment = new CategoryGoodsFragment();
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
        if (goodsGridAdapter!=null){
            mGridView.setAdapter(goodsGridAdapter);
        }else {
            new NewsTask().execute();
        }
        return view;
    }

    private class NewsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            products = ProductApi.getCategoryProducts(category_id);
            if (products != null){
                return true;
            }else{
                return false;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if ((boolean) result){
                goodsGridAdapter = new GoodsGridAdapter((MainActivity)getActivity(), products);
                mGridView.setAdapter(goodsGridAdapter);
            }
        }
    }
}