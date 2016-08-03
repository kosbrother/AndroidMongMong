package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.adpters.PastOrdersAdapter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.entity.postorder.PostOrder;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class PastOrderActivity extends BaseActivity {

    GridView mGridView;
    List<PostOrder> postOrders = new ArrayList<>();
    PastOrdersAdapter pastOrdersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_orders);
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
                        if (postOrders != null && postOrders.size() >= 0) {
                            PastOrderActivity.this.postOrders.addAll(postOrders);
                            onGetDataResult();
                        } else {
                            GAManager.sendError("getOrdersByEmailError", listResponseEntity.getError());
                        }
                    }
                });
    }

    private void onGetDataResult() {
        if (pastOrdersAdapter == null) {
            pastOrdersAdapter = new PastOrdersAdapter(PastOrderActivity.this, postOrders);
            mGridView = (GridView) findViewById(R.id.fragment_gridview);
            mGridView.setAdapter(pastOrdersAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(PastOrderActivity.this, PastOrderDetailActivity.class);
                    intent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID,
                            PastOrderActivity.this.postOrders.get(position).getId());
                    startActivity(intent);
                }
            });
        } else {
            pastOrdersAdapter.notifyDataSetChanged();
        }
    }

}
