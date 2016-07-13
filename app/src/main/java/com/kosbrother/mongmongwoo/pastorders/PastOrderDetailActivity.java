package com.kosbrother.mongmongwoo.pastorders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.adpters.PastItemAdapter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.pastorder.Info;
import com.kosbrother.mongmongwoo.entity.pastorder.PastItem;
import com.kosbrother.mongmongwoo.entity.pastorder.PastOrder;
import com.kosbrother.mongmongwoo.fragments.CsBottomSheetDialogFragment;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.customerservice.CustomerServiceClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPickUpOpenedEvent;

import java.util.List;

import rx.functions.Action1;

public class PastOrderDetailActivity extends BaseActivity {

    public static final String EXTRA_INT_ORDER_ID = "EXTRA_INT_ORDER_ID";
    public static final String EXTRA_BOOLEAN_FROM_NOTIFICATION = "EXTRA_BOOLEAN_FROM_NOTIFICATION";

    private CsBottomSheetDialogFragment csBottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        setToolbar();

        int orderId = getIntent().getIntExtra(EXTRA_INT_ORDER_ID, 0);
        Webservice.getPastOrderByOrderId(orderId, new Action1<PastOrder>() {
            @Override
            public void call(PastOrder data) {
                if (data != null) {
                    onGetPostOrderResult(data);
                }
            }
        });

        if (isFromNotification()) {
            GAManager.sendEvent(new NotificationPickUpOpenedEvent("" + orderId));
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromNotification()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    private void onGetPostOrderResult(PastOrder pastOrder) {
        setContentView(R.layout.activity_past_order_detail);
        setToolbar();
        initCsBottomSheet();
        if (pastOrder != null) {
            setOrderStatusLayout(pastOrder.getStatus());
            setRecyclerView(pastOrder.getItems());
            setPastOrder(pastOrder);
            setInfo(pastOrder.getInfo());
        }
    }

    private void initCsBottomSheet() {
        csBottomSheetDialogFragment = new CsBottomSheetDialogFragment();
    }

    private void setOrderStatusLayout(String status) {
        FrameLayout container = (FrameLayout) findViewById(R.id.order_status_container);
        assert container != null;
        container.addView(OrderStatusLayoutFactory.create(this, status));
    }

    private void setRecyclerView(List<PastItem> pastOrderProducts) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.order_detail_items_rv);
        assert recyclerView != null;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PastOrderDetailActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        PastItemAdapter adapter = new PastItemAdapter(pastOrderProducts);
        recyclerView.setAdapter(adapter);
    }

    @SuppressWarnings("ConstantConditions")
    private void setPastOrder(PastOrder pastOrder) {
        TextView idTextView = (TextView) findViewById(R.id.order_detail_id_tv);
        idTextView.setText(pastOrder.getIdText());

        TextView itemsPriceTextView = (TextView) findViewById(R.id.order_detail_items_price_tv);
        itemsPriceTextView.setText(pastOrder.getItemsPriceText());

        TextView shipPriceTextView = (TextView) findViewById(R.id.order_detail_ship_price_tv);
        shipPriceTextView.setText(pastOrder.getShipFeeText());

        TextView totalPriceTextView = (TextView) findViewById(R.id.order_detail_total_price_tv);
        totalPriceTextView.setText(pastOrder.getTotalText());

        String note = pastOrder.getNote();
        if (note != null && !note.isEmpty()) {
            ViewStub noteViewStub = (ViewStub) findViewById(R.id.note_vs);
            View viewStub = noteViewStub.inflate();

            TextView noteTextView = (TextView) viewStub.findViewById(R.id.note_tv);
            noteTextView.setText(note);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setInfo(Info info) {
        TextView shipStoreNameTextView = (TextView) findViewById(R.id.order_detail_ship_store_name_tv);
        shipStoreNameTextView.setText(info.getShipStoreName());

        TextView shipStoreAddressTextView = (TextView) findViewById(R.id.order_detail_store_address_tv);
        shipStoreAddressTextView.setText(info.getShipStoreAddress());

        TextView shipStorePhoneTextView = (TextView) findViewById(R.id.order_detail_store_phone_tv);
        shipStorePhoneTextView.setText(info.getShipStorePhone());

        TextView shipNameTextView = (TextView) findViewById(R.id.order_detail_ship_name_tv);
        shipNameTextView.setText(info.getShipName());

        TextView shipPhoneTextView = (TextView) findViewById(R.id.order_detail_ship_phone_tv);
        shipPhoneTextView.setText(info.getShipPhone());

        TextView shipEmailTextView = (TextView) findViewById(R.id.order_detail_ship_email_tv);
        shipEmailTextView.setText(info.getShipEmail());
    }

    public void onCustomerServiceFabClick(View view) {
        csBottomSheetDialogFragment.show(getSupportFragmentManager(), "");
        GAManager.sendEvent(new CustomerServiceClickEvent("FAB"));
    }

    public void onContinueShoppingClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private boolean isFromNotification() {
        return getIntent().getBooleanExtra(EXTRA_BOOLEAN_FROM_NOTIFICATION, false);
    }
}
