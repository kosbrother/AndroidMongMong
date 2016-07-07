package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.adpters.PastOrdersGridAdapter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.model.PastOrder;
import com.kosbrother.mongmongwoo.model.User;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.functions.Action1;

public class PastOrderActivity extends AppCompatActivity {

    User user;

    String TAG = "PastOrderActivity";

    GridView mGridView;
    List<PastOrder> pastOrders = new ArrayList<>();
    PastOrdersGridAdapter pastOrdersAdapter;

    CircularImageView userImage;
    TextView userNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("我的訂單");

        userImage = (CircularImageView) findViewById(R.id.user_imageview);
        userNameText = (TextView) findViewById(R.id.user_name_text);
        user = Settings.getSavedUser();
        Glide.with(PastOrderActivity.this)
                .load(user.getFbPic())
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.mipmap.ic_head)
                .into(userImage);
        userNameText.setText(user.getUserName());

        getOrders();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_query_orders, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        } else if (itemId == R.id.query_item) {
            Intent intent = new Intent(this, QueryPastOrdersActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void getOrders() {
        Webservice.getOrdersByEmail(user.getEmail(),
                new Action1<ResponseEntity<List<PastOrder>>>() {
                    @Override
                    public void call(ResponseEntity<List<PastOrder>> listResponseEntity) {
                        List<PastOrder> pastOrders = listResponseEntity.getData();
                        if (pastOrders != null && pastOrders.size() >= 0) {
                            PastOrderActivity.this.pastOrders.addAll(pastOrders);
                            onGetDataResult();
                        } else {
                            GAManager.sendError("getOrdersByEmailError", listResponseEntity.getError());
                        }
                    }
                });
    }

    private void onGetDataResult() {
        if (pastOrdersAdapter == null) {
            pastOrdersAdapter = new PastOrdersGridAdapter(PastOrderActivity.this, pastOrders);
            mGridView = (GridView) findViewById(R.id.fragment_gridview);
            mGridView.setAdapter(pastOrdersAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(PastOrderActivity.this, PastOrderDetailActivity.class);
                    intent.putExtra(PastOrderDetailActivity.EXTRA_INT_ORDER_ID,
                            PastOrderActivity.this.pastOrders.get(position).getId());
                    startActivity(intent);
                }
            });
        } else {
            pastOrdersAdapter.notifyDataSetChanged();
        }
    }

}
