package com.kosbrother.mongmongwoo.myshoppingpoints.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsDetailEntity;

import java.util.List;

public class ShoppingPointsDetailFragment extends Fragment implements ShoppingPointsDetailView {

    public static final String ARG_SHOPPING_POINTS_INFO = "ARG_SHOPPING_POINTS_INFO";

    private RecyclerView recyclerView;
    private ShoppingPointsDetailPresenter presenter;
    private TextView totalTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShoppingPointsDetailEntity shoppingPointsDetailEntity = (ShoppingPointsDetailEntity) getArguments().get(ARG_SHOPPING_POINTS_INFO);
        ShoppingPointsDetailModel shoppingPointsDetailModel =
                new ShoppingPointsDetailModel(shoppingPointsDetailEntity);
        presenter = new ShoppingPointsDetailPresenter(this, shoppingPointsDetailModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_points_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        totalTextView = (TextView) view.findViewById(R.id.fragment_shopping_points_detail_total_tv);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_shopping_points_detail_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        presenter.onViewCreated();
    }

    @Override
    public void setTotalTextView(String totalText) {
        totalTextView.setText(totalText);
    }

    @Override
    public void setRecyclerViewEmpty() {
        recyclerView.setAdapter(new ShoppingPointsDetailEmptyAdapter());
    }

    @Override
    public void setRecyclerView(List<ShoppingPointsViewModel> viewModels) {
        recyclerView.setAdapter(new ShoppingPointsDetailAdapter(viewModels));
    }
}
