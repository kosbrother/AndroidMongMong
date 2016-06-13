package com.kosbrother.mongmongwoo.shoppingcart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.facebook.FbLoginActivity;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment1;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment2;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment3;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment4;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep1EnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep2ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep2EnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep3ClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep3EnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.checkout.CheckoutStep4EnterEvent;
import com.kosbrother.mongmongwoo.googleanalytics.label.GALabel;
import com.kosbrother.mongmongwoo.model.Order;
import com.kosbrother.mongmongwoo.model.PostProduct;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;
import com.kosbrother.mongmongwoo.utils.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCarActivity extends FbLoginActivity {

    NonSwipeableViewPager viewPager;
    Order theOrder;

    TextView breadCrumb1;
    TextView breadCrumb2;
    TextView breadCrumb3;
    private TextView breadCrumb4;

    LinearLayout breadCrumbsLayout;

    LinearLayout no_net_layout;

    private List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_car);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("結帳");

        breadCrumb1 = (TextView) findViewById(R.id.bread_crumbs_1_text);
        breadCrumb2 = (TextView) findViewById(R.id.bread_crumbs_2_text);
        breadCrumb3 = (TextView) findViewById(R.id.bread_crumbs_3_text);
        breadCrumb4 = (TextView) findViewById(R.id.bread_crumbs_4_text);
        breadCrumbsLayout = (LinearLayout) findViewById(R.id.bread_crumbs_layout);
        no_net_layout = (LinearLayout) findViewById(R.id.no_net_layout);

        initOrder();

        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        GAManager.sendEvent(new CheckoutStep1EnterEvent());

                        breadCrumb1.setBackgroundResource(R.drawable.circle_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
                        break;
                    case 1:
                        GAManager.sendEvent(new CheckoutStep2EnterEvent());

                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
                        break;
                    case 2:
                        GAManager.sendEvent(new CheckoutStep3EnterEvent());

                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
                        break;
                    case 3:
                        invalidateOptionsMenu();
                        GAManager.sendEvent(new CheckoutStep4EnterEvent());

                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_style);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        GAManager.sendEvent(new CheckoutStep1EnterEvent());
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
        if (viewPager.getCurrentItem() == 3) {
            menu.findItem(R.id.previous).setVisible(false);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        switch (currentItem) {
            case 1:
                GAManager.sendEvent(new CheckoutStep2ClickEvent(GALabel.PREVIOUS_STEP));
                viewPager.setCurrentItem(currentItem - 1, true);
                break;
            case 2:
                GAManager.sendEvent(new CheckoutStep3ClickEvent(GALabel.PREVIOUS_STEP));
                viewPager.setCurrentItem(currentItem - 1, true);
                break;
            default:
                super.onBackPressed();
                break;
        }
    }

    public void setBreadCurmbsVisibility(int view_param) {
        breadCrumbsLayout.setVisibility(view_param);
    }

    public void saveStoreInfo(Store store) {
        theOrder.setStore(store);
        theOrder.setShipStoreCode(store.getStoreCode());
        theOrder.setShipStoreId(store.getId());
        theOrder.setShipStoreName(store.getName());
    }

    public void performClickFbButton() {
        startFbLogin();
    }

    public Order getOrder() {
        return theOrder;
    }

    public void saveProducts(List<Product> shoppingCarProducts) {
        products = shoppingCarProducts;
    }

    public void savePostProducts(List<PostProduct> products) {
        theOrder.setProducts(products);
    }

    public void savePrice(int totalGoodsPrice, int shippingPrice) {
        theOrder.setItemsPrice(totalGoodsPrice);
        theOrder.setShipFee(shippingPrice);
        theOrder.setTotal(shippingPrice + totalGoodsPrice);
    }

    public void saveShippingInfo(String shipName, String shipPhone, String shipEmail) {
        theOrder.setShipName(shipName);
        theOrder.setShipPhone(shipPhone);
        theOrder.setShipEmail(shipEmail);
    }

    public void startPurchaseFragment2() {
        ((PurchaseFragment1) getViewPagerAdapter().getItem(0)).updateLayoutByLoginStatus();
        ((PurchaseFragment2) getViewPagerAdapter().getItem(1)).setEmailLayoutByLoginStatus();
        viewPager.setCurrentItem(1, true);
    }

    public void startPurchaseFragment3() {
        viewPager.setCurrentItem(2, true);
    }

    public void startPurchaseFragment4() {
        ((PurchaseFragment4) getViewPagerAdapter().getItem(3)).setThankYouMessage();
        viewPager.setCurrentItem(3, true);
    }

    @Override
    public void onFbRequestCompleted(String fb_uid, String user_name, String picUrl) {
        theOrder.setUid(fb_uid);
        startPurchaseFragment2();
    }

    @Override
    public void onFbLogout() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtil.getConnectivityStatus(this) == 0) {
            no_net_layout.setVisibility(View.VISIBLE);
        } else {
            no_net_layout.setVisibility(View.GONE);
        }
    }

    private void initOrder() {
        theOrder = new Order();
        if (Settings.getSavedStore() != null) {
            saveStoreInfo(Settings.getSavedStore());
        }
        theOrder.setUid(Settings.checkIsLogIn() ? Settings.getSavedUser().getFb_uid() : "9999");
        theOrder.setShipName(Settings.getShippingName());
        theOrder.setShipPhone(Settings.getShippingPhone());
        theOrder.setRegistrationId(FirebaseInstanceId.getInstance().getToken());
    }

    private SampleFragmentPagerAdapter getViewPagerAdapter() {
        return (SampleFragmentPagerAdapter) viewPager.getAdapter();
    }

    public List<Product> getProducts() {
        return products;
    }

    public class SampleFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentList.add(PurchaseFragment1.newInstance());
            mFragmentList.add(PurchaseFragment2.newInstance());
            mFragmentList.add(PurchaseFragment3.newInstance());
            mFragmentList.add(PurchaseFragment4.newInstance());
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }

}
