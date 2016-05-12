package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kosbrother.mongmongwoo.adpters.MyCollectAdapter;
import com.kosbrother.mongmongwoo.model.Product;

import java.util.List;

public class MyCollectActivity extends AppCompatActivity implements MyCollectAdapter.MyCollectListener {

    private View emptyView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        setToolbar();
        emptyView = findViewById(R.id.my_collect_empty_include);
        recyclerView = (RecyclerView) findViewById(R.id.my_collect_recycler_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Product> productList = MyCollectManager.getCollectedList(this);
        setContentView(productList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onCollectItemClick(int position) {
        List<Product> productList = MyCollectManager.getCollectedList(this);

        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra(ProductActivity.EXTRA_SERIALIZABLE_PRODUCT, productList.get(position));
        intent.putExtra(ProductActivity.EXTRA_BOOLEAN_FROM_MY_COLLECT, true);
        startActivity(intent);
    }

    @Override
    public void onRemoveItemClick(int position) {
        List<Product> productList = MyCollectManager.removeProductFromCollectedList(this, position);

        setContentView(productList);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setContentView(List<Product> productList) {
        if (productList == null || productList.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            setRecyclerView(productList);
        }
    }

    private void setRecyclerView(List<Product> productList) {
        MyCollectAdapter adapter = getAdapter();
        if (adapter == null) {
            adapter = new MyCollectAdapter(productList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } else {
            adapter.update(productList);
            adapter.notifyDataSetChanged();
        }
    }

    private MyCollectAdapter getAdapter() {
        return (MyCollectAdapter) recyclerView.getAdapter();
    }
}