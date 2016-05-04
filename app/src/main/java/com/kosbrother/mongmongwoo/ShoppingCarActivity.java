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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment1;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment2;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment3;
import com.kosbrother.mongmongwoo.fragments.PurchaseFragment4;
import com.kosbrother.mongmongwoo.model.Order;
import com.kosbrother.mongmongwoo.model.Product;
import com.kosbrother.mongmongwoo.model.Store;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;
import com.kosbrother.mongmongwoo.utils.NonSwipeableViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kolichung on 3/9/16.
 */
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
    Tracker mTracker;

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

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

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
                        menuItem.setTitle("上一步");
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
                        sendShoppingFragment(1);
                        break;
                    case 1:
                        menuItem.setTitle("上一步");
                        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                viewPager.setCurrentItem(0, true);
                                return true;
                            }
                        });
                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
                        sendShoppingFragment(2);
                        break;
                    case 2:
                        menuItem.setTitle("上一步");
                        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                viewPager.setCurrentItem(1, true);
                                return true;
                            }
                        });
                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_non_select_style);
                        sendShoppingFragment(3);
                        break;
                    case 3:
                        menuItem.setVisible(false);
                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb4.setBackgroundResource(R.drawable.circle_style);
                        sendShoppingFragment(4);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        sendShoppingFragment(1);
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
        theOrder.getOrderProducts().clear();
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
    protected void onFbRequestCompleted(String fb_uid, String user_name, String picUrl) {
        theOrder.setUid(fb_uid);
        startPurchaseFragment2();
    }

    @Override
    protected void onFbLogout() {

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

    private void sendShoppingFragment(int fragmentPosition) {
        mTracker.setScreenName("Shopping Fragment " + Integer.toString(fragmentPosition));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void initOrder() {
        theOrder = new Order();
        if (Settings.getSavedStore(this) != null) {
            theOrder.setShippingStore(Settings.getSavedStore(this));
            theOrder.setShippingName(Settings.getShippingName(this));
            theOrder.setShippingPhone(Settings.getShippingPhone(this));
        }
        if (Settings.checkIsLogIn(this)) {
            theOrder.setUid(Settings.getSavedUser(this).getFb_uid());
        }
    }

    private SampleFragmentPagerAdapter getViewPagerAdapter() {
        return (SampleFragmentPagerAdapter) viewPager.getAdapter();
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
