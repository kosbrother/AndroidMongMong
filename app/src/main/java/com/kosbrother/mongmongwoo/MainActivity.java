package com.kosbrother.mongmongwoo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.widget.LoginButton;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.kosbrother.mongmongwoo.api.UrlCenter;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.appindex.AppIndexManager;
import com.kosbrother.mongmongwoo.entity.AndroidVersionEntity;
import com.kosbrother.mongmongwoo.facebook.FbLoginActivity;
import com.kosbrother.mongmongwoo.fivestars.FiveStarsActivity;
import com.kosbrother.mongmongwoo.fivestars.FiveStartsManager;
import com.kosbrother.mongmongwoo.fragments.CsBottomSheetDialogFragment;
import com.kosbrother.mongmongwoo.fragments.GoodsGridFragment;
import com.kosbrother.mongmongwoo.googleanalytics.GAManager;
import com.kosbrother.mongmongwoo.googleanalytics.event.cart.CartClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.customerservice.CustomerServiceClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.navigationdrawer.NavigationDrawerClickEvent;
import com.kosbrother.mongmongwoo.googleanalytics.event.navigationdrawer.NavigationDrawerOpenEvent;
import com.kosbrother.mongmongwoo.model.User;
import com.kosbrother.mongmongwoo.mycollect.MyCollectActivity;
import com.kosbrother.mongmongwoo.pastorders.PastOrderActivity;
import com.kosbrother.mongmongwoo.pastorders.QueryPastOrdersActivity;
import com.kosbrother.mongmongwoo.utils.MongMongWooUtil;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCarActivity;
import com.kosbrother.mongmongwoo.shoppingcart.ShoppingCarPreference;
import com.kosbrother.mongmongwoo.utils.NetworkUtil;
import com.kosbrother.mongmongwoo.utils.ShareUtil;
import com.kosbrother.mongmongwoo.utils.VersionUtil;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.functions.Action1;

public class MainActivity extends FbLoginActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CircularImageView userImage;
    private TextView userText;
    private LoginButton loginButton;
    private RelativeLayout spotLightShoppingCarLayout;

    private ViewPager viewPager;
    private CsBottomSheetDialogFragment csBottomSheetDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppIndexManager.init(this);
        csBottomSheetDialogFragment = new CsBottomSheetDialogFragment();

        setToolbarAndDrawer();
        setSpotLightShoppingCarLayout();
        setSpotLightConfirmButton();
        setNavigationView();
        setViewPagerAndTabLayout();
        setLoginButton(loginButton);
        checkAndroidVersion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppIndexManager.startMainAppIndex();
    }

    @Override
    protected void onStop() {
        AppIndexManager.stopMainAppIndex();
        super.onStop();
    }

    @Override
    public void onFbRequestCompleted(String fb_uid, String user_name, String picUrl) {
        setUserLoinView(user_name, picUrl);
    }

    @Override
    public void onFbLogout() {
        setUserLogoutView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (FiveStartsManager.getInstance(this).showFiveStarsRecommend()) {
                startActivity(new Intent(this, FiveStarsActivity.class));
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        setNoNetLayout();

        User user = Settings.getSavedUser();
        String userName = user.getUserName();

        if (userName != null && !userName.isEmpty()) {
            setUserLoinView(userName, user.getFb_pic());
        } else {
            setUserLogoutView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem shoppingCartItem = menu.findItem(R.id.shopping_cart);
        View shoppingCartView = MenuItemCompat.getActionView(shoppingCartItem);
        shoppingCartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GAManager.sendEvent(new CartClickEvent());

                Intent shoppingCarIntent = new Intent(MainActivity.this, ShoppingCarActivity.class);
                startActivity(shoppingCarIntent);
            }
        });
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.shopping_cart);
        View countView = MenuItemCompat.getActionView(item);
        TextView countTextView = (TextView) countView.findViewById(R.id.count);

        ShoppingCarPreference pref = new ShoppingCarPreference();
        int count = pref.getShoppingCarItemSize(this);
        if (count == 0) {
            countTextView.setVisibility(View.GONE);
        } else {
            countTextView.setText(String.valueOf(count));
            countTextView.setVisibility(View.VISIBLE);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        GAManager.sendEvent(new NavigationDrawerClickEvent(item.getTitle().toString()));

        int id = item.getItemId();
        if (id == R.id.nav_orders) {
            if (Settings.checkIsLogIn()) {
                if (NetworkUtil.getConnectivityStatus(MainActivity.this) != 0) {
                    Intent intent = new Intent(MainActivity.this, PastOrderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "無網路連線", Toast.LENGTH_SHORT).show();
                }
            } else {
                startActivity(new Intent(this, QueryPastOrdersActivity.class));
            }
        } else if (id == R.id.nav_service) {
            startActivity(new Intent(this, ServiceActivity.class));
        } else if (id == R.id.nav_collect) {
            startActivity(new Intent(this, MyCollectActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.nav_share) {
            String title = "分享萌萌屋";
            String subject = "萌萌屋 - 走在青年流行前線";
            String text = UrlCenter.GOOGLE_PLAY_SHARE;
            ShareUtil.shareText(this, title, subject, text);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressWarnings("UnusedParameters")
    public void onCustomerServiceFabClick(View view) {
        csBottomSheetDialogFragment.show(getSupportFragmentManager(), "");
        GAManager.sendEvent(new CustomerServiceClickEvent("FAB"));
    }

    public void showShoppingCarInstruction() {
        spotLightShoppingCarLayout.setVisibility(View.VISIBLE);
    }

    private void checkAndroidVersion() {
        Webservice.getAndroidVersion(new Action1<AndroidVersionEntity>() {
            @Override
            public void call(AndroidVersionEntity version) {
                onGetVersionResult(version);
            }
        });
    }

    private void onGetVersionResult(AndroidVersionEntity version) {
        if (version == null) {
            return;
        }
        boolean upToDate = VersionUtil.isVersionUpToDate(version.getVersionCode());
        String version_name = version.getVersionName();
        Settings.saveAndroidVersion(version_name, upToDate);

        NavigationView navigationView = getNavigationView();
        Menu navigationViewMenu = navigationView.getMenu();
        View lastVersionActionView = navigationViewMenu.findItem(R.id.nav_about).getActionView();

        if (upToDate) {
            lastVersionActionView.setVisibility(View.INVISIBLE);
            Settings.resetNotUpdateTimes();
        } else {
            lastVersionActionView.setVisibility(View.VISIBLE);
            if (VersionUtil.remindUpdate()) {
                showUpdateDialog(version_name, version.getUpdateMessage());
                Settings.resetNotUpdateTimes();
            }
            Settings.addNotUpdateTimes();
        }
    }

    private void setToolbarAndDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleText.setText("萌萌屋");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                GAManager.sendEvent(new NavigationDrawerOpenEvent());
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setSpotLightShoppingCarLayout() {
        spotLightShoppingCarLayout = (RelativeLayout) findViewById(R.id.spotlight_shopping_car_layout);
        spotLightShoppingCarLayout.setVisibility(View.INVISIBLE);
    }

    private void setSpotLightConfirmButton() {
        Button spotLightConfirmButton = (Button) findViewById(R.id.confirm_button);
        spotLightConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotLightShoppingCarLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setNavigationView() {
        NavigationView navigationView = getNavigationView();
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        userImage = (CircularImageView) header.findViewById(R.id.user_imageview);
        userText = (TextView) header.findViewById(R.id.user_name_text);
        loginButton = (LoginButton) header.findViewById(R.id.login_button_main);
    }

    private void setViewPagerAndTabLayout() {
        SampleFragmentPagerAdapter adapter = new SampleFragmentPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                SampleFragmentPagerAdapter adapter = (SampleFragmentPagerAdapter) viewPager.getAdapter();
                GAManager.sendScreen("Fragment~" + adapter.getPageTitle(position).toString());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setNoNetLayout() {
        LinearLayout no_net_layout = (LinearLayout) findViewById(R.id.no_net_layout);
        if (NetworkUtil.getConnectivityStatus(this) == 0) {
            no_net_layout.setVisibility(View.VISIBLE);
        } else {
            no_net_layout.setVisibility(View.GONE);
        }
    }

    private void setUserLoinView(String user_name, String picUrl) {
        loginButton.setVisibility(View.GONE);
        userText.setText(user_name);
        Glide.with(this)
                .load(picUrl)
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.icon_head)
                .into(userImage);

        MenuItem ordersMenuItem = getNavigationView().getMenu().findItem(R.id.nav_orders);
        ordersMenuItem.setTitle("我的訂單");
    }

    private void setUserLogoutView() {
        userImage.setImageResource(R.drawable.icon_head);
        userText.setText("");
        loginButton.setVisibility(View.VISIBLE);

        MenuItem ordersMenuItem = getNavigationView().getMenu().findItem(R.id.nav_orders);
        ordersMenuItem.setTitle("查詢訂單");
    }

    private NavigationView getNavigationView() {
        return (NavigationView) findViewById(R.id.nav_view);
    }

    @SuppressLint("InflateParams")
    private void showUpdateDialog(String version_name, String update_message) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_version, null);
        ((TextView) dialogView.findViewById(R.id.version_name_tv)).setText(version_name);
        ((TextView) dialogView.findViewById(R.id.update_msg_tv)).setText(update_message);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(dialogView);
        dialog.show();

        dialogView.findViewById(R.id.update_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MongMongWooUtil.startToGooglePlayPage(MainActivity.this);
                dialog.dismiss();
            }
        });
    }

    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            addFragment(GoodsGridFragment.newInstance(10, "所有商品"), " 所有商品 ");
            addFragment(GoodsGridFragment.newInstance(11, "新品上架"), " 新品上架 ");
            addFragment(GoodsGridFragment.newInstance(12, "文具用品"), " 文具用品 ");
            addFragment(GoodsGridFragment.newInstance(13, "日韓精選"), " 日韓精選 ");
            addFragment(GoodsGridFragment.newInstance(14, "生日專區"), " 生日專區 ");
            addFragment(GoodsGridFragment.newInstance(16, "生活小物"), " 生活小物 ");
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
            return mFragmentTitleList.get(position);
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }

}
