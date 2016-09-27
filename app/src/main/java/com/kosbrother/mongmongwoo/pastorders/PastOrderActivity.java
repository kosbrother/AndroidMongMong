package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.adpters.PastOrdersAdapter;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.postorder.PostOrder;

import java.util.List;

public class PastOrderActivity extends BaseActivity implements DataManager.ApiCallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        setToolbar();

        getOrders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_query_orders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.query_item) {
            Intent intent = new Intent(this, QueryPastOrdersActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onDestroy() {
        DataManager.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object data) {
        onGetDataResult((List<PostOrder>) data);
    }

    private void getOrders() {
        String email = Settings.getSavedUser().getEmail();
        DataManager.getInstance().getOrdersByEmail(email, this);
    }

    private void onGetDataResult(final List<PostOrder> postOrders) {
        if (postOrders.size() == 0) {
            setContentView(R.layout.activity_past_orders_empty);
            setToolbar();
        } else {
            setContentView(R.layout.activity_past_orders);
            setToolbar();

            PastOrdersAdapter pastOrdersAdapter = new PastOrdersAdapter(this, postOrders);
            GridView mGridView = (GridView) findViewById(R.id.fragment_gridview);
            mGridView.setAdapter(pastOrdersAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(PastOrderActivity.this, PastOrderDetailActivity.class);
                    intent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID,
                            postOrders.get(position).getId());
                    intent.putExtra(PastOrderDetailActivity.EXTRA_BOOLEAN_FROM_MY_ORDERS, true);
                    startActivity(intent);
                }
            });

        }
    }
}
