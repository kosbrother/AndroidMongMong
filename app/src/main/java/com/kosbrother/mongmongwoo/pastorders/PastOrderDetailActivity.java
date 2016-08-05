package com.kosbrother.mongmongwoo.pastorders;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.MainActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.adpters.PastItemAdapter;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.entity.pastorder.Info;
import com.kosbrother.mongmongwoo.entity.pastorder.PastItem;
import com.kosbrother.mongmongwoo.entity.pastorder.PastOrder;
import com.kosbrother.mongmongwoo.fragments.CsBottomSheetDialogFragment;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.customerservice.CustomerServiceClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.notification.NotificationPickUpOpenedEvent;
import com.kosbrother.mongmongwoo.widget.CenterProgressDialog;

import java.util.List;

public class PastOrderDetailActivity extends BaseActivity implements DataManager.ApiCallBack {

    public static final String EXTRA_INT_ORDER_ID = "EXTRA_INT_ORDER_ID";
    public static final String EXTRA_BOOLEAN_FROM_NOTIFICATION = "EXTRA_BOOLEAN_FROM_NOTIFICATION";

    private CsBottomSheetDialogFragment csBottomSheetDialogFragment;
    private PastOrder pastOrder;
    private CenterProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        if (isFromNotification()) {
            GAManager.sendEvent(new NotificationPickUpOpenedEvent("" + getOrderId()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.loading_no_toolbar);
        DataManager.getInstance().getOrder(getOrderId(), this);
    }

    @Override
    protected void onDestroy() {
        DataManager.getInstance().unSubscribe(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.past_order_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (pastOrder != null) {
            MenuItem cancelOrderItem = menu.findItem(R.id.past_order_detail_cancel_order_item);
            cancelOrderItem.setVisible(pastOrder.isCancelable());
            return true;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.past_order_detail_cancel_order_item) {
            onCancelOrderClick();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        if (isFromNotification()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void onGetPostOrderResult(PastOrder pastOrder) {
        this.pastOrder = pastOrder;
        setContentView(R.layout.activity_past_order_detail);
        initCsBottomSheet();
        setOrderStatusLayout(pastOrder.getStatus());
        setRecyclerView(pastOrder.getItems());
        setPastOrder(pastOrder);
        setInfo(pastOrder.getInfo());
        invalidateOptionsMenu();

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

    @Override
    public void onError(String errorMessage) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(Object data) {
        if (data instanceof PastOrder) {
            onGetPostOrderResult((PastOrder) data);
        } else if (data instanceof String) {
            progressDialog.dismiss();
            onResume();
        }
    }

    private void onCancelOrderClick() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("取消訂單");
        alertDialogBuilder.setMessage("是否確定要取消這次的訂單？");
        alertDialogBuilder.setNegativeButton("取消", null);
        alertDialogBuilder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onConfirmCancelOrderClick();
            }
        });
        alertDialogBuilder.show();
    }

    private void onConfirmCancelOrderClick() {
        progressDialog = CenterProgressDialog.show(this, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onAbortCancelOrder();
            }
        });
        int userId = Settings.getSavedUser().getUserId();
        DataManager.getInstance().cancelOrder(userId, pastOrder.getId(), this);
    }

    private void onAbortCancelOrder() {
        DataManager.getInstance().unSubscribe(this);
        onResume();
    }

    private boolean isFromNotification() {
        return getIntent().getBooleanExtra(EXTRA_BOOLEAN_FROM_NOTIFICATION, false);
    }

    private int getOrderId() {
        return getIntent().getIntExtra(EXTRA_INT_ORDER_ID, 0);
    }
}
