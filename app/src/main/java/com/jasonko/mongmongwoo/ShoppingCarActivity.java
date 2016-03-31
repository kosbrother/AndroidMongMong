package com.jasonko.mongmongwoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jasonko.mongmongwoo.api.UserApi;
import com.jasonko.mongmongwoo.fragments.PurchaseFragment1;
import com.jasonko.mongmongwoo.fragments.PurchaseFragment2;
import com.jasonko.mongmongwoo.fragments.PurchaseFragment3;
import com.jasonko.mongmongwoo.model.Order;
import com.jasonko.mongmongwoo.model.Product;
import com.jasonko.mongmongwoo.model.Store;
import com.jasonko.mongmongwoo.model.User;
import com.jasonko.mongmongwoo.utils.NetworkUtil;
import com.jasonko.mongmongwoo.utils.NonSwipeableViewPager;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kolichung on 3/9/16.
 */
public class ShoppingCarActivity extends AppCompatActivity {

    String TAG = "ShoppingCarActivity";

    NonSwipeableViewPager viewPager;
    MenuItem menuItem;
    Order theOrder;

//    LinearLayout loginLayout;
    TextView breadCrumb1;
    TextView breadCrumb2;
    TextView breadCrumb3;
    LinearLayout breadCrumbsLayout;

    AccessTokenTracker accessTokenTracker;
    LoginButton loginButton;
    CallbackManager callbackManager;

    String user_name = "";
    String real_name ="";
    String gender = "";
    String phone = "";
    String address = "";
    String fb_uid = "";

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
        breadCrumbsLayout = (LinearLayout) findViewById(R.id.bread_crumbs_layout);
        no_net_layout = (LinearLayout) findViewById(R.id.no_net_layout);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button_main);
        //        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i("Facebook", "success login" + " id: " + loginResult.getAccessToken().getUserId());
                viewPager.setCurrentItem(1);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login

                        Bundle bFacebookData = getFacebookData(object);
                        try {
                            String picUrl = bFacebookData.getString("profile_pic");

                            user_name = bFacebookData.getString("name");
                            fb_uid = bFacebookData.getString("idFacebook");
                            gender = bFacebookData.getString("gender");
                            new PostUserTask().execute();

                            User theUser = new User(user_name, "", gender, "", "", fb_uid, picUrl);
                            Settings.saveUserFBData(ShoppingCarActivity.this, theUser);
                            getOrder().setUid(fb_uid);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name,gender");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                Log.i("Facebook", "cancel login");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.i("Facebook", "error login");
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                Log.d(TAG, "onCurrentAccessTokenChanged()");
                if (oldAccessToken == null) {
                    Log.i(TAG, "Facebook login");
                } else if (newAccessToken == null) {
                    Log.i(TAG, "Facebook logout");
                    Settings.clearAllUserData(ShoppingCarActivity.this);
                }
            }
        };


        theOrder = new Order();
        if (Settings.getSavedStore(this)!= null){
            theOrder.setShippingStore(Settings.getSavedStore(this));
            theOrder.setShippingName(Settings.getShippingName(this));
            theOrder.setShippingPhone(Settings.getShippingPhone(this));
        }

        viewPager = (NonSwipeableViewPager) findViewById(R.id.viewpager);
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

                switch (position){
                    case 0:
                        menuItem.setTitle("返回購物");
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
                        break;
                    case 1:
                        menuItem.setTitle("上一步");
                        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                viewPager.setCurrentItem(0);
                                return true;
                            }
                        });
                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_non_select_style);
                        break;
                    case 2:
                        menuItem.setTitle("上一步");
                        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                viewPager.setCurrentItem(1);
                                return true;
                            }
                        });
                        breadCrumb1.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb2.setBackgroundResource(R.drawable.circle_non_select_style);
                        breadCrumb3.setBackgroundResource(R.drawable.circle_style);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setBreadCurmbsVisibility(int view_param){
        breadCrumbsLayout.setVisibility(view_param);
    }

    public void setPagerPostition(int postition){
        viewPager.setCurrentItem(postition);
    }

    public void setSelectedStore(Store store){
        theOrder.setShippingStore(store);
    }

    public void performClickFbButton(){
        loginButton.performClick();
    }

    public Order getOrder(){
        return theOrder;
    }

    public void saveOrderProducts(ArrayList<Product> products){
        theOrder.getOrderProducts().clear();
        theOrder.setOrderProducts(products);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuItem = menu.add("返回購物");
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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        if (menuItem.getItemId() == android.R.id.home) {
//            finish();
//        }
        return super.onOptionsItemSelected(menuItem);
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[]{"確認金額", "下一步", "送出訂單"};

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment newFragment;
            switch (position){
                case 0:
                    newFragment = PurchaseFragment1.newInstance();
                    break;
                case 1:
                    newFragment = PurchaseFragment2.newInstance();
                    break;
                default:
                    newFragment = PurchaseFragment3.newInstance();
                    break;
            }
            return newFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.i("MainActivity", "activity result");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        if (NetworkUtil.getConnectivityStatus(this) == 0){
            no_net_layout.setVisibility(View.VISIBLE);
        }else {
            no_net_layout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=200");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("name"))
                bundle.putString("name", object.getString("name"));
//            if (object.has("email"))
//                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));

            return bundle;
        }catch (Exception e){
            return null;
        }
    }

    private class PostUserTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String result = UserApi.httpPostUser(user_name, real_name, gender, phone, address, fb_uid);
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            if( result.toString().equals("success") ){
                Log.i(TAG,"成功上傳");
            }else {
                Log.i(TAG,"上傳失敗");
            }
        }
    }

    public void sendShoppoingFragment(int fragmentPosition){
        mTracker.setScreenName("Shopping Fragment " + Integer.toString(fragmentPosition));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
