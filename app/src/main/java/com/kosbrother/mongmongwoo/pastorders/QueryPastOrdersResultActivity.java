package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.PastOrdersGridAdapter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.model.PastOrder;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class QueryPastOrdersResultActivity extends AppCompatActivity {

    public static final String EXTRA_STRING_EMAIL = "EXTRA_STRING_EMAIL";
    public static final String EXTRA_STRING_PHONE = "EXTRA_STRING_PHONE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_past_orders_result);
        setToolbar();
        initGridView();
        requestOrders();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @SuppressWarnings("ConstantConditions")
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initGridView() {
        final GridView ordersGridView = getOrdersGridView();
        ordersGridView.setAdapter(new PastOrdersGridAdapter(this, new ArrayList<PastOrder>()));
        ordersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onOrderItemClick(position);
            }
        });
    }

    private void onOrderItemClick(int position) {
        PastOrdersGridAdapter adapter = (PastOrdersGridAdapter) getOrdersGridView().getAdapter();

        Intent intent = new Intent(QueryPastOrdersResultActivity.this, PastOrderDetailActivity.class);
        intent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID, adapter.getItem(position).getId());
        startActivity(intent);
    }

    private void requestOrders() {
        Intent intent = getIntent();
        String email = intent.getStringExtra(EXTRA_STRING_EMAIL);
        String phone = intent.getStringExtra(EXTRA_STRING_PHONE);
        Webservice.getOrdersByEmailAndPhone(email, phone, new Action1<List<PastOrder>>() {
            @Override
            public void call(List<PastOrder> pastOrders) {
                onGetOrdersResult(pastOrders);
            }
        });
    }

    private void onGetOrdersResult(List<PastOrder> pastOrders) {
        if (pastOrders == null) {
            return;
        }

        GridView ordersGridView = getOrdersGridView();
        PastOrdersGridAdapter adapter = (PastOrdersGridAdapter) ordersGridView.getAdapter();
        adapter.updateOrders(pastOrders);
        adapter.notifyDataSetChanged();
    }

    private GridView getOrdersGridView() {
        return (GridView) findViewById(R.id.orders_gv);
    }
}
