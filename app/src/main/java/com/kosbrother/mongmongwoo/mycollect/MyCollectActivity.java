package com.kosbrother.mongmongwoo.mycollect;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kosbrother.mongmongwoo.ProductActivity;
import com.kosbrother.mongmongwoo.R;
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
        Product product = productList.get(position);

        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra(ProductActivity.EXTRA_INT_PRODUCT_ID, product.getId());
        intent.putExtra(ProductActivity.EXTRA_INT_CATEGORY_ID, product.getCategoryId());
        intent.putExtra(ProductActivity.EXTRA_STRING_CATEGORY_NAME, product.getCategoryName());
        startActivity(intent);
    }

    @Override
    public void onCancelCollectClick(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("取消收藏");
        alertDialogBuilder.setMessage("是否確定要取消收藏");
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<Product> productList = MyCollectManager.removeProductFromCollectedList(
                        getApplicationContext(), position);
                setContentView(productList);
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
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
