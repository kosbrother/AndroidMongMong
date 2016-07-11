package com.kosbrother.mongmongwoo.shoppingcart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.SelectDeliverStoreActivity;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment1;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment2;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment3;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment4;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep2ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep3ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.login.LoginActivity;
import com.kosbrother.mongmongwoo.model.Order;
import com.kosbrother.mongmongwoo.model.PastOrder;
import com.kosbrother.mongmongwoo.model.PostProduct;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.utils.KeyboardUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class ShoppingCarActivity extends BaseActivity implements
        PurchaseFragment1.OnStep1ButtonClickListener,
        PurchaseFragment2.OnStep2ButtonClickListener,
        PurchaseFragment3.OnStpe3ButtonClickListener {

    private static final int REQUEST_LOGIN = 111;
    private static final int REQUEST_SELECT_STORE = REQUEST_LOGIN + 1;

    private long mLastClickTime = 0;

    private Order theOrder;

    private View breadCrumb1;
    private View breadCrumb2;
    private View breadCrumb3;
    private View breadCrumb4;

    private List<Product> products;
    private int totalGoodsPrice;
    private int shippingPrice;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_car);
        setToolbarWithoutNavIcon("結帳");

        products = ShoppingCartManager.getInstance().loadShoppingItems();
        if (products.size() == 0) {
            onNoShoppingItem();
            return;
        }

        findStepBarView();
        initOrder();
        startStep1(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.previous) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof PurchaseFragment3) {
            menu.findItem(R.id.previous).setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();

        int backStackEntryCount = supportFragmentManager.getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            switch (backStackEntryCount) {
                case 1:
                    GAManager.sendEvent(new CheckoutStep2ClickEvent(GALabel.PREVIOUS_STEP));
                    setStepBar1();
                    break;
                case 2:
                    GAManager.sendEvent(new CheckoutStep3ClickEvent(GALabel.PREVIOUS_STEP));
                    setStepBar2();
                    break;
                default:
                    break;
            }
            supportFragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    onLoginResultOk(data);
                }
                break;
            case REQUEST_SELECT_STORE:
                if (resultCode == RESULT_OK) {
                    onSelectStoreResultOk(data);
                }
                break;
        }
    }

    @Override
    public void onGuestCheckoutClick(int totalGoodsPrice, int shippingPrice) {
        this.totalGoodsPrice = totalGoodsPrice;
        this.shippingPrice = shippingPrice;
        onStartStep2();
    }

    @Override
    public void onLoginClick(final int totalGoodsPrice, final int shippingPrice) {
        this.totalGoodsPrice = totalGoodsPrice;
        this.shippingPrice = shippingPrice;
        onStartLogin();
    }

    @Override
    public void onConfirmButtonClick(int totalGoodsPrice, int shippingPrice) {
        this.totalGoodsPrice = totalGoodsPrice;
        this.shippingPrice = shippingPrice;
        onStartStep2();
    }

    @Override
    public void onNoShoppingItem() {
        ViewStub emptyShoppingCar = (ViewStub) findViewById(R.id.empty_shopping_car_vs);
        emptyShoppingCar.inflate();
        findViewById(R.id.bread_crumbs_layout).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSelectStoreClick() {
        Intent selectStoreIntent = new Intent(this, SelectDeliverStoreActivity.class);
        startActivityForResult(selectStoreIntent, REQUEST_SELECT_STORE);
    }

    @Override
    public void onStep2NextButtonClick(String shipName, String shipPhone, String shipEmail) {
        saveShippingInfo(shipName, shipPhone, shipEmail);
        Settings.saveUserShippingNameAndPhone(shipName, shipPhone);

        View view = getCurrentFocus();
        if (view != null) {
            KeyboardUtil.hide(this, view);
        }

        startStep3();
    }

    @Override
    public void onSendOrderClick() {
        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        showProgressDialog();
        GAManager.sendEvent(new CheckoutStep3ClickEvent(GALabel.SEND_ORDER));
        requestPostOrder();
    }

    public List<Product> getProducts() {
        return products;
    }

    private void findStepBarView() {
        breadCrumb1 = findViewById(R.id.bread_crumbs_1_text);
        breadCrumb2 = findViewById(R.id.bread_crumbs_2_text);
        breadCrumb3 = findViewById(R.id.bread_crumbs_3_text);
        breadCrumb4 = findViewById(R.id.bread_crumbs_4_text);
    }

    private void initOrder() {
        theOrder = new Order();
        if (Settings.getSavedStore() != null) {
            saveStoreInfo(Settings.getSavedStore());
        }
        String email = Settings.getEmail();
        theOrder.setEmail(email.isEmpty() ? "anonymous@mmwooo.fake.com" : email);
        theOrder.setRegistrationId(FirebaseInstanceId.getInstance().getToken());
    }

    private void onStartLogin() {
        if (totalGoodsPrice < shippingPrice) {
            showPriceAlertDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startLoginActivity();
                }
            });
        } else {
            startLoginActivity();
        }
    }

    private void onStartStep2() {
        if (totalGoodsPrice < shippingPrice) {
            showPriceAlertDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startStep2();
                }
            });
        } else {
            startStep2();
        }
    }

    private void onLoginResultOk(Intent data) {
        String email = data.getStringExtra(LoginActivity.EXTRA_STRING_EMAIL);
        theOrder.setEmail(email);

        PurchaseFragment1 purchaseFragment1 =
                (PurchaseFragment1) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        purchaseFragment1.updateLayoutByLoginStatus();
        startStep2();
    }

    private void onSelectStoreResultOk(Intent data) {
        Store theStore = (Store) data.getSerializableExtra(SelectDeliverStoreActivity.EXTRA_SELECTED_STORE);
        saveStoreInfo(theStore);
        Settings.saveUserStoreData(theStore);

        PurchaseFragment2 purchaseFragment2 =
                (PurchaseFragment2) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        purchaseFragment2.updateStoreName(theStore.getName());
    }

    private void saveStoreInfo(Store store) {
        theOrder.setShipStoreCode(store.getStoreCode());
        theOrder.setShipStoreId(store.getId());
        theOrder.setShipStoreName(store.getName());
    }

    private void savePostProductsAndPrice() {
        theOrder.setProducts(getPostProducts());
        theOrder.setItemsPrice(totalGoodsPrice);
        theOrder.setShipFee(shippingPrice);
        theOrder.setTotal(shippingPrice + totalGoodsPrice);
    }

    private void saveShippingInfo(String shipName, String shipPhone, String shipEmail) {
        theOrder.setShipName(shipName);
        theOrder.setShipPhone(shipPhone);
        theOrder.setShipEmail(shipEmail);
    }

    private void startStep1(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }

        PurchaseFragment1 firstFragment = new PurchaseFragment1();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, firstFragment)
                .commit();
    }

    private void startStep2() {
        savePostProductsAndPrice();
        setStepBar2();

        PurchaseFragment2 purchaseFragment2 = new PurchaseFragment2();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, purchaseFragment2)
                .addToBackStack(null)
                .commit();
    }

    private void startStep3() {
        setStepBar3();

        PurchaseFragment3 purchaseFragment3 = new PurchaseFragment3();
        Bundle args = new Bundle();
        args.putSerializable(PurchaseFragment3.ARG_SERIALIZABLE_ORDER, theOrder);
        args.putSerializable(PurchaseFragment3.ARG_SERIALIZABLE_PRODUCTS, (Serializable) products);
        purchaseFragment3.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, purchaseFragment3)
                .addToBackStack(null)
                .commit();
    }

    private void startStep4(int id) {
        invalidateOptionsMenu();
        setStepBar4();

        PurchaseFragment4 purchaseFragment4 = new PurchaseFragment4();
        Bundle args = new Bundle();
        args.putInt(PurchaseFragment4.ARG_INT_ORDER_ID, id);
        purchaseFragment4.setArguments(args);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, purchaseFragment4)
                .commit();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private List<PostProduct> getPostProducts() {
        List<PostProduct> postProductList = new ArrayList<>();
        for (Product product : products) {
            PostProduct postProduct = new PostProduct();
            postProduct.setName(product.getName());
            postProduct.setProductId(product.getId());
            postProduct.setSpecId(product.getSelectedSpec().getId());
            postProduct.setStyle(product.getSelectedSpec().getStyle());
            postProduct.setQuantity(product.getBuy_count());
            postProduct.setPrice(product.getFinalPrice());
            postProductList.add(postProduct);
        }
        return postProductList;
    }

    private void showPriceAlertDialog(DialogInterface.OnClickListener onConfirmClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("購買金額低於運費")
                .setMessage("提醒您，您所購買的金額低於運費60元，是否確認購買")
                .setCancelable(false)
                .setPositiveButton("確認", onConfirmClickListener)
                .setNegativeButton("再逛逛", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void requestPostOrder() {
        String json = new Gson().toJson(theOrder);
        Webservice.postOrder(json, new Action1<ResponseEntity<PastOrder>>() {
            @Override
            public void call(ResponseEntity<PastOrder> stringResponseEntity) {
                hideProgressDialog();
                PastOrder data = stringResponseEntity.getData();
                if (data == null) {
                    GAManager.sendError("postOrderError", stringResponseEntity.getError());
                    Toast.makeText(ShoppingCarActivity.this, "訂單未成功送出，資料異常", Toast.LENGTH_SHORT).show();
                } else {
                    ShoppingCartManager.getInstance().removeAllShoppingItems();
                    startStep4(data.getId());
                }
            }
        }, new Action1<IOException>() {
            @Override
            public void call(IOException e) {
                hideProgressDialog();
                Toast.makeText(ShoppingCarActivity.this, "網路發生異常，請檢查網路是否連線", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setStepBar1() {
        breadCrumb1.setBackgroundResource(R.drawable.circle_style);
        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
    }

    private void setStepBar2() {
        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb2.setBackgroundResource(R.drawable.circle_style);
        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
    }

    private void setStepBar3() {
        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb3.setBackgroundResource(R.drawable.circle_style);
        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
    }

    private void setStepBar4() {
        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb4.setBackgroundResource(R.drawable.circle_style);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("送出訂單...");
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
