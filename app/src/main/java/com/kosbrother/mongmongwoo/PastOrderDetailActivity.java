package com.kosbrother.mongmongwoo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.adpters.PastOrderListAdapter;
import com.kosbrother.mongmongwoo.api.OrderApi;
import com.kosbrother.mongmongwoo.model.PastOrder;

public class PastOrderDetailActivity extends AppCompatActivity {

    public static final String EXTRA_INT_ORDER_ID = "EXTRA_INT_ORDER_ID";

    private RecyclerView recyclerView;
    private TextView shippingPriceText;
    private TextView totalPriceText;
    private TextView shippingNameText;
    private TextView shippingPhoneText;
    private TextView shippingStoreNameText;

    private TextView order_id_text;
    private TextView order_status_text;
    private TextView order_ship_way;
    private ViewStub noteViewStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        setToolbar();
        findViews();
        setRecyclerView();

        int orderId = getIntent().getIntExtra(EXTRA_INT_ORDER_ID, 0);
        new getPastOrderByOrderIdTask().execute(orderId);
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
        getSupportActionBar().setTitle("訂單明細");
    }

    private void findViews() {
        shippingPriceText = (TextView) findViewById(R.id.fragment3_shipping_price_text);
        totalPriceText = (TextView) findViewById(R.id.fragment3_total_price_text);
        shippingNameText = (TextView) findViewById(R.id.fragment3_shipping_name_text);
        shippingPhoneText = (TextView) findViewById(R.id.fragment3_shipping_phone_text);
        shippingStoreNameText = (TextView) findViewById(R.id.fragment3_shipping_store_name_text);
        order_id_text = (TextView) findViewById(R.id.order_detail_num_text);
        order_status_text = (TextView) findViewById(R.id.order_detail_status_text);
        order_ship_way = (TextView) findViewById(R.id.order_detail_ship_way);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_buy_goods);
        noteViewStub = (ViewStub) findViewById(R.id.note_vs);
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PastOrderDetailActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void onGetPostOrderResult(PastOrder pastOrder) {
        if (pastOrder != null) {
            PastOrderListAdapter adapter = new PastOrderListAdapter(pastOrder.getPastOrderProducts());
            recyclerView.setAdapter(adapter);

            String shipFeeString = Integer.toString(pastOrder.getShipFee());
            shippingPriceText.setText(shipFeeString);

            String totalPriceString = Integer.toString(pastOrder.getTotal());
            totalPriceText.setText(totalPriceString);

            String shipNameString = "收件人：" + pastOrder.getShipName();
            shippingNameText.setText(shipNameString);

            String phoneString = "電話：" + pastOrder.getShipPhone();
            shippingPhoneText.setText(phoneString);

            String orderIdString = "訂單編號：" + pastOrder.getOrder_id();
            order_id_text.setText(orderIdString);

            shippingStoreNameText.setText(pastOrder.getShippingStore().getName());
            order_status_text.setText(pastOrder.getStatus());
            order_ship_way.setText("運送方式：超商取貨");

            String note = pastOrder.getNote();
            if (note != null && !note.isEmpty()) {
                TextView noteTextView = (TextView) noteViewStub.inflate();
                String noteString = "備註：" + note;
                noteTextView.setText(noteString);
            }
        }
    }

    private class getPastOrderByOrderIdTask extends AsyncTask<Integer, Void, PastOrder> {

        @Override
        protected PastOrder doInBackground(Integer... params) {
            return OrderApi.getPastOrderByOrderId(params[0]);
        }

        @Override
        protected void onPostExecute(PastOrder result) {
            onGetPostOrderResult(result);
        }
    }

}
