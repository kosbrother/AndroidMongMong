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
import com.kosbrother.mongmongwoo.adpters.PastOrderListAdapter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.fragments.CsBottomSheetDialogFragment;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.customerservice.CustomerServiceClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPickUpOpenedEvent;
import com.kosbrother.mongmongwoo.model.PastOrder;
import com.kosbrother.mongmongwoo.model.PostProduct;

import java.util.List;

import rx.functions.Action1;

public class PastOrderDetailActivity extends BaseActivity {

    public static final String EXTRA_INT_ORDER_ID = "EXTRA_INT_ORDER_ID";
    public static final String EXTRA_BOOLEAN_FROM_NOTIFICATION = "EXTRA_BOOLEAN_FROM_NOTIFICATION";

    private CsBottomSheetDialogFragment csBottomSheetDialogFragment;

    private RecyclerView recyclerView;
    private TextView shippingPriceText;
    private TextView totalPriceText;
    private TextView shippingNameText;
    private TextView shippingPhoneText;
    private TextView shippingStoreNameText;

    private TextView order_id_text;
    private TextView order_ship_way;
    private ViewStub noteViewStub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        setToolbar();

        int orderId = getIntent().getIntExtra(EXTRA_INT_ORDER_ID, 0);
        Webservice.getPastOrderByOrderId(orderId, new Action1<ResponseEntity<PastOrder>>() {
            @Override
            public void call(ResponseEntity<PastOrder> pastOrderResponseEntity) {
                PastOrder result = pastOrderResponseEntity.getData();
                if (result == null) {
                    GAManager.sendError("getPastOrderByOrderIdError", pastOrderResponseEntity.getError());
                } else {
                    onGetPostOrderResult(result);
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
        setContentView(R.layout.activity_order_detail);
        setToolbar();
        initCsBottomSheet();
        if (pastOrder != null) {
            findViews();
            setOrderStatusLayout(pastOrder.getStatus());
            setRecyclerView(pastOrder.getItems());
            setPastOrderData(pastOrder);
        }
    }

    private void initCsBottomSheet() {
        csBottomSheetDialogFragment = new CsBottomSheetDialogFragment();
    }

    private void findViews() {
        shippingPriceText = (TextView) findViewById(R.id.fragment3_shipping_price_text);
        totalPriceText = (TextView) findViewById(R.id.fragment3_total_price_text);
        shippingNameText = (TextView) findViewById(R.id.fragment3_shipping_name_text);
        shippingPhoneText = (TextView) findViewById(R.id.fragment3_shipping_phone_text);
        shippingStoreNameText = (TextView) findViewById(R.id.fragment3_shipping_store_name_text);
        order_id_text = (TextView) findViewById(R.id.order_detail_num_text);
        order_ship_way = (TextView) findViewById(R.id.order_detail_ship_way);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_buy_goods);
        noteViewStub = (ViewStub) findViewById(R.id.note_vs);
    }

    private void setOrderStatusLayout(String status) {
        FrameLayout container = (FrameLayout) findViewById(R.id.order_status_container);
        container.addView(OrderStatusLayoutFactory.create(this, status));
    }

    private void setRecyclerView(List<PostProduct> pastOrderProducts) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PastOrderDetailActivity.this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        PastOrderListAdapter adapter = new PastOrderListAdapter(pastOrderProducts);
        recyclerView.setAdapter(adapter);
    }

    private void setPastOrderData(PastOrder pastOrder) {
        String shipFeeString = Integer.toString(pastOrder.getShipFee());
        shippingPriceText.setText(shipFeeString);

        String totalPriceString = Integer.toString(pastOrder.getTotal());
        totalPriceText.setText(totalPriceString);

        String shipNameString = "收件人：" + pastOrder.getInfo().getShipName();
        shippingNameText.setText(shipNameString);

        String phoneString = "電話：" + pastOrder.getInfo().getShipPhone();
        shippingPhoneText.setText(phoneString);

        String orderIdString = "訂單編號：" + pastOrder.getId();
        order_id_text.setText(orderIdString);

        shippingStoreNameText.setText(pastOrder.getInfo().getShipStoreName());

        order_ship_way.setText("運送方式：超商取貨");

        String note = pastOrder.getNote();
        if (note != null && !note.isEmpty()) {
            TextView noteTextView = (TextView) noteViewStub.inflate();
            String noteString = "備註：" + note;
            noteTextView.setText(noteString);
        }
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
