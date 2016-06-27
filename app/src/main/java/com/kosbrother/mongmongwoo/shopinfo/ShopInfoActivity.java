package com.kosbrother.mongmongwoo.shopinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ShopInfoEntity;

import java.util.List;

import rx.functions.Action1;

public class ShopInfoActivity extends BaseActivity implements Action1<List<ShopInfoEntity>> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        setToolbar();
        Webservice.getShopInfos(this);
    }

    @Override
    public void call(List<ShopInfoEntity> shopInfoEntities) {
        setContentView(R.layout.activity_shop_infos);
        setToolbar();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.shop_info_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShopInfoActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ShopInfoAdapter(shopInfoEntities));
    }
}
