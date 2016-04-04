package com.kosbrother.mongmongwoo.fragments;

import android.os.AsyncTask;
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
import com.kosbrother.mongmongwoo.api.ProductApi;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.utils.EndlessScrollListener;

import java.util.ArrayList;

/**
 * Created by kolichung on 3/1/16.
 */
public class GoodsGridFragment  extends Fragment {

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
                new NewsTask().execute();
            }
        });

        if (goodsGridAdapter!=null){
            layoutProgress.setVisibility(View.GONE);
            mGridView.setAdapter(goodsGridAdapter);
        }else {
            layoutProgress.setVisibility(View.VISIBLE);
            new NewsTask().execute();
        }
        return view;
    }

    private class NewsTask extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {
            ArrayList<Product> feedBackProducts = ProductApi.getCategoryProducts(category_id, page);
            if (feedBackProducts!=null && feedBackProducts.size()>0) {
                products.addAll(feedBackProducts);
                page = page + 1;
                return true;
            }else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            layoutProgress.setVisibility(View.GONE);
            if ((boolean) result){
                if (goodsGridAdapter == null) {
                    MainActivity activity = (MainActivity) getActivity();
                    goodsGridAdapter = new GoodsGridAdapter(activity, products);
                    mGridView.setAdapter(goodsGridAdapter);
                }else {
                    goodsGridAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void notifyCategoryChanged(int category_id){

        this.category_id = category_id;
        products.clear();
        layoutProgress.setVisibility(View.VISIBLE);
        page = 1;
        new NewsTask().execute();

        try {
            MainActivity activity = (MainActivity) getActivity();
            activity.sendFragmentCategoryName(category_id);
        }catch (Exception e){

        }
    }

    public int getProductsSize(){
        if (products!=null) {
            return products.size();
        }else {
            return 0;
        }
    }

}
