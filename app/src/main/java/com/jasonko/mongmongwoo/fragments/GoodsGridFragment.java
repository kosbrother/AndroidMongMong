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
 * Created by kolichung on 3/1/16.
 */
public class GoodsGridFragment  extends Fragment {

    private GridView mGridView;
    private GoodsGridAdapter movieGridAdapter;

    ArrayList<Product> products;

    public static GoodsGridFragment newInstance() {
        GoodsGridFragment fragment = new GoodsGridFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grid, container, false);
        mGridView = (GridView) view.findViewById(R.id.fragment_gridview);
        new NewsTask().execute();
        return view;
    }

    private class NewsTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            products = ProductApi.getCategoryProducts(1);
            if (products!=null) {
                return true;
            }else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            if ((boolean) result){
                MainActivity activity = (MainActivity) getActivity();
                movieGridAdapter = new GoodsGridAdapter(activity, products);
                mGridView.setAdapter(movieGridAdapter);
            }
        }
    }
}
