package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.entity.postorder.PostOrder;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;

import java.util.List;

import rx.functions.Action1;

public class PastOrderActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.loading_no_toolbar);
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

    private void getOrders() {
        String email = Settings.getSavedUser().getEmail();
        Webservice.getOrdersByEmail(email,
                new Action1<ResponseEntity<List<PostOrder>>>() {
                    @Override
                    public void call(ResponseEntity<List<PostOrder>> listResponseEntity) {
                        List<PostOrder> postOrders = listResponseEntity.getData();
                        if (postOrders != null) {
                            onGetDataResult(postOrders);
                        } else {
                            ResponseEntity.Error error = listResponseEntity.getError();
                            Toast.makeText(PastOrderActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            GAManager.sendError("getOrdersByEmailError", error);
                        }
                    }
                });
    }

    private void onGetDataResult(final List<PostOrder> postOrders) {
        if (postOrders.size() == 0) {
            setContentView(R.layout.activity_past_orders_empty);
        } else {
            setContentView(R.layout.activity_past_orders);
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
