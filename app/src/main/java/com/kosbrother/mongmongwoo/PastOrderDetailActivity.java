package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.adpters.PastOrderListAdapter;
import com.kosbrother.mongmongwoo.api.DensityApi;
import com.kosbrother.mongmongwoo.api.WebService;
import com.kosbrother.mongmongwoo.model.PastOrder;

import rx.functions.Action1;

/**
 * Created by kolichung on 3/28/16.
 */
public class PastOrderDetailActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView shippingPriceText;
    TextView totalPriceText;
    TextView shippingNameText;
    TextView shippingPhoneText;
    TextView shippingStoreNameText;

    TextView order_id_text;
    TextView order_status_text;
    TextView order_ship_way;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        Intent theIntent = getIntent();
        Bundle bundle = theIntent.getExtras();
        PastOrder thePastOrder = (PastOrder) bundle.getSerializable("THE_ORDER");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("訂單明細");

        shippingPriceText = (TextView) findViewById(R.id.fragment3_shipping_price_text);
        totalPriceText = (TextView) findViewById(R.id.fragment3_total_price_text);
        shippingNameText = (TextView) findViewById(R.id.fragment3_shipping_name_text);
        shippingPhoneText = (TextView) findViewById(R.id.fragment3_shipping_phone_text);
        shippingStoreNameText = (TextView) findViewById(R.id.fragment3_shipping_store_name_text);

        order_id_text = (TextView) findViewById(R.id.order_detail_num_text);
        order_status_text = (TextView) findViewById(R.id.order_detail_status_text);
        order_ship_way = (TextView) findViewById(R.id.order_detail_ship_way);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_buy_goods);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PastOrderDetailActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        WebService.getPastOrderByOrderId(thePastOrder, new Action1<PastOrder>() {
            @Override
            public void call(PastOrder pastOrder) {
                if (pastOrder == null) {
                    return;
                }
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = (int) DensityApi.convertDpToPixel(
                        50 * pastOrder.getPastOrderProducts().size(), PastOrderDetailActivity.this);
                recyclerView.setLayoutParams(params);

                PastOrderListAdapter adapter = new PastOrderListAdapter(
                        PastOrderDetailActivity.this, pastOrder.getPastOrderProducts());
                recyclerView.setAdapter(adapter);

                shippingPriceText.setText(Integer.toString(pastOrder.getShipPrice()));
                totalPriceText.setText(Integer.toString(pastOrder.getTotal_price()));
                shippingNameText.setText("收件人：" + pastOrder.getShippingName());
                shippingPhoneText.setText(pastOrder.getShippingPhone());
                shippingStoreNameText.setText(pastOrder.getShippingStore().getName());

                order_id_text.setText("訂單編號：" + Integer.toString(pastOrder.getOrder_id()));
                order_status_text.setText(pastOrder.getStatus());
                order_ship_way.setText("運送方式：超商取貨");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
