package com.kosbrother.mongmongwoo.myshoppingpoints.campaign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.entity.myshoppingpoints.ShoppingPointsCampaignsEntity;

import java.util.List;

public class ShoppingPointsCampaignFragment extends Fragment implements ShoppingPointsCampaignView {

    public static final String ARG_SHOPPING_POINTS_CAMPAIGNS = "ARG_SHOPPING_POINTS_CAMPAIGNS";

    private ShoppingPointsCampaignPresenter presenter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<ShoppingPointsCampaignsEntity> shoppingPointsCampaignsEntities =
                (List<ShoppingPointsCampaignsEntity>) getArguments().get(ARG_SHOPPING_POINTS_CAMPAIGNS);
        ShoppingPointsCampaignModel model = new ShoppingPointsCampaignModel(shoppingPointsCampaignsEntities);
        presenter = new ShoppingPointsCampaignPresenter(this, model);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_points_campaign, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view;
        presenter.onViewCreated();
    }

    @Override
    public void setRecyclerView(List<ShoppingPointsCampaignViewModel> shoppingPointsCampaignViewModels) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ShoppingPointsCampaignAdapter(shoppingPointsCampaignViewModels));
    }
}
