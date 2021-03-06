package com.kosbrother.mongmongwoo.shoppingcart;

import android.app.ProgressDialog;
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
import com.kosbrother.mongmongwoo.BaseActivity;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.checkout.CheckoutFragment;
import com.kosbrother.mongmongwoo.checkout.CheckoutStep2Fragment;
import com.kosbrother.mongmongwoo.checkout.HomeDeliveryFragment;
import com.kosbrother.mongmongwoo.checkout.MpgActivity;
import com.kosbrother.mongmongwoo.checkout.Step1Fragment;
import com.kosbrother.mongmongwoo.checkout.StoreDeliveryFragment;
import com.kosbrother.mongmongwoo.checkout.ThankYouActivity;
import com.kosbrother.mongmongwoo.entity.checkout.MpgEntity;
import com.kosbrother.mongmongwoo.entity.checkout.OrderPrice;
import com.kosbrother.mongmongwoo.entity.mycollect.PostWishListsEntity;
import com.kosbrother.mongmongwoo.entity.postorder.PostOrderResultEntity;
import com.kosbrother.mongmongwoo.entity.postorder.UnableToBuyModel;
import com.kosbrother.mongmongwoo.facebookevent.FacebookLogger;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep2ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep3ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.model.Order;
import com.kosbrother.mongmongwoo.model.PostProduct;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.ShipType;
import com.kosbrother.mongmongwoo.model.Store;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class ShoppingCarActivity extends BaseActivity implements
        Step1Fragment.OnStep1ButtonClickListener,
        CheckoutStep2Fragment.OnStep2ButtonClickListener,
        CheckoutFragment.OnStep3ButtonClickListener {

    private static final int REQUEST_MPG_PAYMENT = 222;

    private long mLastClickTime = 0;

    private Order theOrder;

    private View breadCrumb1;
    private View breadCrumb2;
    private View breadCrumb3;

    private ProgressDialog progressDialog;

    private DataManager.ApiCallBack postOrderCallBack = new DataManager.ApiCallBack() {
        @Override
        public void onError(String errorMessage) {
            hideProgressDialog();
            Toast.makeText(ShoppingCarActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            setSendButtonEnabled(true);
        }

        @Override
        public void onSuccess(Object data) {
            hideProgressDialog();
            if (data instanceof PostOrderResultEntity) {
                PostOrderResultEntity result = (PostOrderResultEntity) data;
                onPostOrderSuccess(result);
            }
        }
    };
    private OrderPrice orderPrice;
    private String shipTypeText;
    private Store store;
    private String shipAddress;
    private MpgEntity mpgEntity;
    private int orderId;
    private boolean spendShoppingPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_car);
        setToolbarWithoutNavIcon();

        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
        if (products == null || products.size() == 0) {
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
    public void onNoShoppingItem() {
        ViewStub emptyShoppingCar = (ViewStub) findViewById(R.id.empty_shopping_car_vs);
        emptyShoppingCar.inflate();
        findViewById(R.id.bread_crumbs_layout).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStep1NextButtonClick(String email, String shipTypeText, boolean isSpendShoppingPoint) {
        this.shipTypeText = shipTypeText;
        this.spendShoppingPoint = isSpendShoppingPoint;

        theOrder.setEmail(email.isEmpty() ? "anonymous@mmwooo.fake.com" : email);
        theOrder.setShipType(ShipType.getShipTypeFromShipTypeText(shipTypeText));

        startStep2(shipTypeText);
    }

    @Override
    public void onStep2NextButtonClick(Store store, String shipAddress, String shipName, String shipPhone, String shipEmail) {
        this.store = store;
        this.shipAddress = shipAddress;

        setOrderStoreInfo();
        setOrderShipInfo(shipName, shipPhone, shipEmail, shipAddress);
        if (isHomeDeliveryByCreditCard()) {
            mpgEntity = new MpgEntity();
            mpgEntity.setEmail(shipEmail);
        }

        FacebookLogger.getInstance().logAddedPaymentInfoEvent(true);

        startStep3();
    }

    @Override
    public void onStep2CheckFailed() {
        onBackPressed();
    }

    @Override
    public void onSendOrderClick(OrderPrice orderPrice) {
        this.orderPrice = orderPrice;
        setSendButtonEnabled(false);
        // mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            setSendButtonEnabled(true);
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        setOrderProductsAndPrice();
        showProgressDialog();
        GAManager.sendEvent(new CheckoutStep3ClickEvent(GALabel.SEND_ORDER));
        DataManager.getInstance().postOrders(theOrder, postOrderCallBack);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MPG_PAYMENT) {
            if (resultCode == RESULT_OK) {
                logPurchasedEvent();
                ShoppingCartManager.getInstance().removeAllShoppingItems();
                startThankYouActivityThenFinish(orderId, Settings.getShipName());
            } else {
                setSendButtonEnabled(true);
            }
        }
    }

    private void findStepBarView() {
        breadCrumb1 = findViewById(R.id.bread_crumbs_1_text);
        breadCrumb2 = findViewById(R.id.bread_crumbs_2_text);
        breadCrumb3 = findViewById(R.id.bread_crumbs_3_text);
    }

    private void initOrder() {
        theOrder = new Order();
        theOrder.setRegistrationId(FirebaseInstanceId.getInstance().getToken());
    }

    private void setOrderStoreInfo() {
        theOrder.setShipStoreCode(store.getStoreCode());
        theOrder.setShipStoreId(store.getId());
        theOrder.setShipStoreName(store.getName());
    }

    private void setOrderProductsAndPrice() {
        theOrder.setProducts(getPostProducts());
        theOrder.setItemsPrice(orderPrice.getOriginItemsPrice());
        theOrder.setShipFee(orderPrice.getShipFee());
        theOrder.setTotal(orderPrice.getTotal());
        theOrder.setShoppingPointsAmount(orderPrice.getShoppingPoint().getUsedAmount());
    }

    private void setOrderShipInfo(String shipName, String shipPhone, String shipEmail, String shipAddress) {
        theOrder.setShipName(shipName);
        theOrder.setShipPhone(shipPhone);
        theOrder.setShipEmail(shipEmail);
        theOrder.setShipAddress(shipAddress);
    }

    private void startStep1(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }

        Step1Fragment firstFragment = new Step1Fragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, firstFragment)
                .commit();
    }

    private void startStep2(String delivery) {
        setStepBar2();
        Fragment purchaseFragment2;
        if (delivery.equals(getString(R.string.dialog_delivery_store))) {
            purchaseFragment2 = new StoreDeliveryFragment();
        } else {
            purchaseFragment2 = new HomeDeliveryFragment();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, purchaseFragment2)
                .addToBackStack(null)
                .commit();
    }

    private void startStep3() {
        setStepBar3();

        CheckoutFragment checkoutFragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putBoolean(CheckoutFragment.ARG_BOOLEAN_SPEND_SHOPPING_POINT, spendShoppingPoint);
        args.putSerializable(CheckoutFragment.ARG_SERIALIZABLE_STORE, store);
        args.putString(CheckoutFragment.ARG_STRING_SHIP_ADDRESS, shipAddress);
        args.putString(CheckoutFragment.ARG_STRING_DELIVERY, shipTypeText);
        checkoutFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, checkoutFragment)
                .addToBackStack(null)
                .commit();
    }

    private void startThankYouActivityThenFinish(int orderId, String shipName) {
        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra(ThankYouActivity.EXTRA_INT_ORDER_ID, orderId);
        intent.putExtra(ThankYouActivity.EXTRA_STRING_SHIP_NAME, shipName);
        startActivity(intent);
        finish();
    }

    private List<PostProduct> getPostProducts() {
        List<PostProduct> postProductList = new ArrayList<>();
        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
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

    private void onPostOrderSuccess(PostOrderResultEntity result) {
        orderId = result.getId();
        if (orderId != 0) {
            if (isHomeDeliveryByCreditCard()) {
                Intent intent = new Intent(this, MpgActivity.class);
                mpgEntity.setTimeStamp(String.valueOf(System.currentTimeMillis()));
                mpgEntity.setMerchantOrderNo(String.valueOf(orderId));
                mpgEntity.setItemDesc("萌萌屋訂單(" + orderId + ")");
                mpgEntity.setAmt(orderPrice.getTotal());
                intent.putExtra(MpgActivity.EXTRA_BYTE_ARRAY_POST_DATA, mpgEntity.getPostData());
                intent.putExtra(MpgActivity.EXTRA_INT_ORDER_ID, orderId);
                startActivityForResult(intent, REQUEST_MPG_PAYMENT);
            } else {
                logPurchasedEvent();
                ShoppingCartManager.getInstance().removeAllShoppingItems();
                startThankYouActivityThenFinish(orderId, Settings.getShipName());
            }
        } else {
            StockShortageDialog dialog = new StockShortageDialog(
                    this, result.getUnableToBuyModels(), new Action1<List<UnableToBuyModel>>() {
                @Override
                public void call(List<UnableToBuyModel> unableToBuyModels) {
                    onStockShortageConfirmClick(unableToBuyModels);
                }
            });
            dialog.show();
            setSendButtonEnabled(true);
        }
    }

    private void logPurchasedEvent() {
        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
        for (Product product : products) {
            FacebookLogger.getInstance().logPurchasedEvent(
                    product.getBuy_count(),
                    product.getCategoryName(),
                    String.valueOf(product.getId()),
                    product.getName(),
                    product.getFinalPrice() * product.getBuy_count()
            );
        }
    }

    private void onStockShortageConfirmClick(List<UnableToBuyModel> unableToBuyModels) {
        List<Product> unableToBuyProduct = ShoppingCartManager.getInstance().
                removeUnableToBuyFromShoppingCart(unableToBuyModels);
        postWishListsIfLogin(unableToBuyProduct);

        List<Product> products = ShoppingCartManager.getInstance().loadShoppingItems();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (products.size() == 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            onNoShoppingItem();
        } else {
            supportFragmentManager.popBackStack();
            startStep3();
        }
    }

    private void postWishListsIfLogin(List<Product> unableToBuyProducts) {
        if (Settings.checkIsLogIn()) {
            ArrayList<PostWishListsEntity.WishItemEntity> wishLists = new ArrayList<>();
            for (Product removeProduct : unableToBuyProducts) {
                wishLists.add(new PostWishListsEntity.WishItemEntity(
                        removeProduct.getId(), removeProduct.getSelectedSpec().getId()));
            }
            PostWishListsEntity entity = new PostWishListsEntity(wishLists);
            int userId = Settings.getSavedUser().getUserId();
            DataManager.getInstance().postWishLists(userId, entity, new DataManager.ApiCallBack() {
                @Override
                public void onError(String errorMessage) {

                }

                @Override
                public void onSuccess(Object data) {

                }
            });
        }
    }

    private void setStepBar1() {
        breadCrumb1.setBackgroundResource(R.drawable.circle_style);
        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
    }

    private void setStepBar2() {
        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb2.setBackgroundResource(R.drawable.circle_style);
        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
    }

    private void setStepBar3() {
        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
        breadCrumb3.setBackgroundResource(R.drawable.circle_style);
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, "送出訂單", "送出訂單中，請稍後...", true, false);
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void setSendButtonEnabled(boolean enabled) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof CheckoutFragment) {
            CheckoutFragment fragment3 = (CheckoutFragment) currentFragment;
            fragment3.setSendOrderButtonEnabled(enabled);
        }
    }

    private boolean isHomeDeliveryByCreditCard() {
        return shipTypeText.equals(ShipType.homeByCreditCard.getShipTypeText());
    }
}
