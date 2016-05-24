package com.kosbrother.mongmongwoo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
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
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;
import com.kosbrother.mongmongwoo.utils.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCarActivity extends FbLoginActivity {

    String TAG = "ShoppingCarActivity";

    NonSwipeableViewPager viewPager;
    MenuItem menuItem;
    Order theOrder;

    //    LinearLayout loginLayout;
    TextView breadCrumb1;
    TextView breadCrumb2;
    TextView breadCrumb3;
    private TextView breadCrumb4;

    LinearLayout breadCrumbsLayout;

    LoginButton loginButton;

    LinearLayout no_net_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_car);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.icon_back_white);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("結帳");

        breadCrumb1 = (TextView) findViewById(R.id.bread_crumbs_1_text);
        breadCrumb2 = (TextView) findViewById(R.id.bread_crumbs_2_text);
        breadCrumb3 = (TextView) findViewById(R.id.bread_crumbs_3_text);
        breadCrumb4 = (TextView) findViewById(R.id.bread_crumbs_4_text);
        breadCrumbsLayout = (LinearLayout) findViewById(R.id.bread_crumbs_layout);
        no_net_layout = (LinearLayout) findViewById(R.id.no_net_layout);

        loginButton = (LoginButton) findViewById(R.id.login_button_main);
        setLoginButton(loginButton);

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
                        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                finish();
                                return true;
                            }
                        });
                        breadCrumb1.setBackgroundResource(R.drawable.circle_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
                        GAManager.sendEvent(new CheckoutStep1EnterEvent());
                        break;
                    case 1:
                        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                onStep2PressPreviousStep();
                                return true;
                            }
                        });
                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
                        GAManager.sendEvent(new CheckoutStep2EnterEvent());
                        break;
                    case 2:
                        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                onStep3PressPreviousStep();
                                return true;
                            }
                        });
                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
                        GAManager.sendEvent(new CheckoutStep3EnterEvent());
                        break;
                    case 3:
                        menuItem.setVisible(false);
                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_style);
                        GAManager.sendEvent(new CheckoutStep4EnterEvent());
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        GAManager.sendEvent(new CheckoutStep1EnterEvent());
    }

    public void setBreadCurmbsVisibility(int view_param) {
        breadCrumbsLayout.setVisibility(view_param);
    }

    public void setSelectedStore(Store store) {
        theOrder.setShippingStore(store);
    }

    public void performClickFbButton() {
        loginButton.performClick();
    }

    public Order getOrder() {
        return theOrder;
    }

    public void saveOrderProducts(ArrayList<Product> products) {
        theOrder.setOrderProducts(products);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItem = menu.add("上一步");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        switch (viewPager.getCurrentItem()) {
            case 1:
                onStep2PressPreviousStep();
                break;
            case 2:
                onStep3PressPreviousStep();
                break;
            default:
                super.onBackPressed();
                break;
        }
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
            theOrder.setShippingStore(Settings.getSavedStore());
            theOrder.setShippingName(Settings.getShippingName());
            theOrder.setShippingPhone(Settings.getShippingPhone());
        }
        if (Settings.checkIsLogIn()) {
            theOrder.setUid(Settings.getSavedUser().getFb_uid());
        }
    }

    private SampleFragmentPagerAdapter getViewPagerAdapter() {
        return (SampleFragmentPagerAdapter) viewPager.getAdapter();
    }

    private void onStep2PressPreviousStep() {
        GAManager.sendEvent(new CheckoutStep2ClickEvent(GALabel.PREVIOUS_STEP));
        viewPager.setCurrentItem(0, true);
    }

    private void onStep3PressPreviousStep() {
        GAManager.sendEvent(new CheckoutStep3ClickEvent(GALabel.PREVIOUS_STEP));
        viewPager.setCurrentItem(1, true);
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
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
