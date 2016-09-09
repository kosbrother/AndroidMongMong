package com.kosbrother.mongmongwoo.shoppingcart;

import android.content.DialogInterface;
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
import com.kosbrother.mongmongwoo.checkout.CheckoutStep2Fragment;
import com.kosbrother.mongmongwoo.checkout.HomeDeliveryFragment;
import com.kosbrother.mongmongwoo.checkout.PurchaseFragment1;
import com.kosbrother.mongmongwoo.checkout.PurchaseFragment3;
import com.kosbrother.mongmongwoo.checkout.PurchaseFragment4;
import com.kosbrother.mongmongwoo.checkout.StoreDeliveryFragment;
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
import com.kosbrother.mongmongwoo.utils.CalculateUtil;
import com.kosbrother.mongmongwoo.widget.CenterProgressDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class ShoppingCarActivity extends BaseActivity implements
        PurchaseFragment1.OnStep1ButtonClickListener,
        CheckoutStep2Fragment.OnStep2ButtonClickListener,
        PurchaseFragment3.OnStep3ButtonClickListener {

    private long mLastClickTime = 0;

    private Order theOrder;

    private View breadCrumb1;
    private View breadCrumb2;
    private View breadCrumb3;
    private View breadCrumb4;

    private List<Product> products;

    private CenterProgressDialog progressDialog;

    private DataManager.ApiCallBack postOrderCallBack = new DataManager.ApiCallBack() {
        @Override
        public void onError(String errorMessage) {
            hideProgressDialog();
            Toast.makeText(ShoppingCarActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
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
    private CalculateUtil.OrderPrice orderPrice;
    private Store store;
    private String shipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_car);
        setToolbarWithoutNavIcon("結帳");

        products = ShoppingCartManager.getInstance().loadShoppingItems();
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
    public void onNoShoppingItem() {
        ViewStub emptyShoppingCar = (ViewStub) findViewById(R.id.empty_shopping_car_vs);
        emptyShoppingCar.inflate();
        findViewById(R.id.bread_crumbs_layout).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStep1NextButtonClick(String email, CalculateUtil.OrderPrice orderPrice, String delivery) {
        this.orderPrice = orderPrice;
        theOrder.setEmail(email.isEmpty() ? "anonymous@mmwooo.fake.com" : email);
        theOrder.setShipType(ShipType.fromString(delivery).getShipType());
        setOrderProductsAndPrice();

        startStep2(delivery);
    }

    @Override
    public void onStep2NextButtonClick(Store store, String shipAddress, String shipName, String shipPhone, String shipEmail) {
        this.store = store;
        this.shipAddress = shipAddress;

        setOrderStoreInfo();
        setOrderShipInfo(shipName, shipPhone, shipEmail, shipAddress);

        FacebookLogger.getInstance().logAddedPaymentInfoEvent(true);

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
        DataManager.getInstance().postOrders(theOrder, postOrderCallBack);
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
        theOrder.setRegistrationId(FirebaseInstanceId.getInstance().getToken());
    }

    private void setOrderStoreInfo() {
        theOrder.setShipStoreCode(store.getStoreCode());
        theOrder.setShipStoreId(store.getId());
        theOrder.setShipStoreName(store.getName());
    }

    private void setOrderProductsAndPrice() {
        theOrder.setProducts(getPostProducts());
        theOrder.setItemsPrice(orderPrice.getItemsPrice());
        theOrder.setShipFee(orderPrice.getShipFee());
        theOrder.setTotal(orderPrice.getTotal());
        theOrder.setShoppingPointsAmount(orderPrice.getShoppingPointsAmount());
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

        PurchaseFragment1 firstFragment = new PurchaseFragment1();

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

        PurchaseFragment3 purchaseFragment3 = new PurchaseFragment3();
        Bundle args = new Bundle();
        args.putSerializable(PurchaseFragment3.ARG_SERIALIZABLE_PRODUCTS, (Serializable) products);
        args.putSerializable(PurchaseFragment3.ARG_SERIALIZABLE_ORDER_PRICE, orderPrice);
        args.putSerializable(PurchaseFragment3.ARG_SERIALIZABLE_STORE, store);
        args.putSerializable(PurchaseFragment3.ARG_STRING_SHIP_ADDRESS, shipAddress);
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

    private void onPostOrderSuccess(PostOrderResultEntity result) {
        int orderId = result.getId();
        if (orderId != 0) {
            logPurchasedEvent();
            ShoppingCartManager.getInstance().removeAllShoppingItems();
            startStep4(orderId);
        } else {
            StockShortageDialog dialog = new StockShortageDialog(
                    this, result.getUnableToBuyModels(), new Action1<List<UnableToBuyModel>>() {
                @Override
                public void call(List<UnableToBuyModel> unableToBuyModels) {
                    onStockShortageConfirmClick(unableToBuyModels);
                }
            });
            dialog.show();
        }
    }

    private void logPurchasedEvent() {
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

        products = ShoppingCartManager.getInstance().loadShoppingItems();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (products.size() == 0) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            onNoShoppingItem();
        } else {
            orderPrice = CalculateUtil.calculateOrderPrice(products, orderPrice.getShoppingPointsAmount());
            setOrderProductsAndPrice();

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
        progressDialog = CenterProgressDialog.show(this, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                DataManager.getInstance().unSubscribe(postOrderCallBack);
            }
        });
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
