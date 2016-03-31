package com.jasonko.mongmongwoo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jasonko.mongmongwoo.api.UserApi;
import com.jasonko.mongmongwoo.fragments.GoodsGridFragment;
import com.jasonko.mongmongwoo.model.User;
import com.jasonko.mongmongwoo.utils.NetworkUtil;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    CircularImageView userImage;
    TextView userText;
//    TextView userSettingText;
    String TAG = "MainActivity";
    AccessTokenTracker accessTokenTracker;
    LoginButton loginButton;
    CallbackManager callbackManager;

    String user_name = "";
    String real_name ="";
    String gender = "";
    String phone = "";
    String address = "";
    String fb_uid = "";

    MenuItem menuItem;
    RelativeLayout spotLightShoppingCarLayout;
    Button spotLightConfirmButton;

    TextView titleText;
    int category_id=10; //10所有商品

    GoodsGridFragment goodsGridFragment;
    LinearLayout no_net_layout;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleText.setText("萌萌屋");
        no_net_layout = (LinearLayout) findViewById(R.id.no_net_layout);

                spotLightShoppingCarLayout = (RelativeLayout) findViewById(R.id.spotlight_shopping_car_layout);
        spotLightShoppingCarLayout.setVisibility(View.INVISIBLE);
        spotLightConfirmButton = (Button) findViewById(R.id.confirm_button);
        spotLightConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotLightShoppingCarLayout.setVisibility(View.INVISIBLE);
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        userImage = (CircularImageView) header.findViewById(R.id.user_imageview);
        userText = (TextView) header.findViewById(R.id.user_name_text);

        // Callback registration
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) header.findViewById(R.id.login_button_main);
        //        loginButton.setReadPermissions("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.i("Facebook", "success login" + " id: " + loginResult.getAccessToken().getUserId());

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        loginButton.setVisibility(View.GONE);

                        Bundle bFacebookData = getFacebookData(object);
                        try {
                            String picUrl = bFacebookData.getString("profile_pic");

                            user_name = bFacebookData.getString("name");
                            fb_uid = bFacebookData.getString("idFacebook");
                            gender = bFacebookData.getString("gender");
                            new PostUserTask().execute();

                            User theUser = new User(user_name, "", gender, "", "", fb_uid, picUrl);
                            Settings.saveUserFBData(MainActivity.this, theUser);

                            userText.setText(user_name);
                            Glide.with(MainActivity.this)
                                    .load(picUrl)
                                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                    .placeholder(R.drawable.icon_head)
                                    .crossFade()
                                    .into(userImage);
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
                    userImage.setImageResource(R.drawable.icon_head);
                    userText.setText("");
                    Settings.clearAllUserData(MainActivity.this);
                    loginButton.setVisibility(View.VISIBLE);
                }
            }
        };


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        if (Settings.checkIsLogIn(this)) {
            User savedUser = Settings.getSavedUser(this);
            Glide.with(MainActivity.this)
                    .load(savedUser.getFb_pic())
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .placeholder(R.drawable.icon_head)
                    .crossFade()
                    .into(userImage);
            userText.setText(savedUser.getUser_name());
            loginButton.setVisibility(View.GONE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
        }

        if (menuItem != null){
            ShoppingCarPreference pref = new ShoppingCarPreference();
            int count = pref.getShoppingCarItemSize(MainActivity.this);
            menuItem.setIcon(buildCounterDrawable(count, R.drawable.icon_shopping_car_2));
        }
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);

        if (NetworkUtil.getConnectivityStatus(this) == 0){
            no_net_layout.setVisibility(View.VISIBLE);
        }else {
            no_net_layout.setVisibility(View.GONE);
            if(goodsGridFragment != null && goodsGridFragment.getProductsSize()==0){
                goodsGridFragment.notifyCategoryChanged(category_id);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menu.findItem(99)==null) {
            menuItem = menu.add(0, 99, 0, "購物車");
        }else {
            menuItem = menu.findItem(99);
        }
        ShoppingCarPreference pref = new ShoppingCarPreference();
        int count = pref.getShoppingCarItemSize(MainActivity.this);
        menuItem.setIcon(buildCounterDrawable(count, R.drawable.icon_shopping_car_2));
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent shoppingCarIntent = new Intent(MainActivity.this, ShoppingCarActivity.class);
                startActivity(shoppingCarIntent);
                return true;
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            if (Settings.checkIsLogIn(this)){
                if (NetworkUtil.getConnectivityStatus(MainActivity.this)!=0) {
                    Intent intent = new Intent(MainActivity.this, PastOrderActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "請先使用FB登入", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_category1) {
            titleText.setText("所有商品");
            category_id = 10;
            if (NetworkUtil.getConnectivityStatus(MainActivity.this)!=0) {
                goodsGridFragment.notifyCategoryChanged(category_id);
            }else {
                Toast.makeText(MainActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_category2) {
            titleText.setText("新品上架");
            category_id = 11;
            if (NetworkUtil.getConnectivityStatus(MainActivity.this)!=0) {
                goodsGridFragment.notifyCategoryChanged(category_id);
            }else {
                Toast.makeText(MainActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_category3) {
            titleText.setText("文具用品");
            category_id = 12;
            if (NetworkUtil.getConnectivityStatus(MainActivity.this)!=0) {
                goodsGridFragment.notifyCategoryChanged(category_id);
            }else {
                Toast.makeText(MainActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_category4) {
            titleText.setText("日韓精選");
            category_id = 13;
            if (NetworkUtil.getConnectivityStatus(MainActivity.this)!=0) {
                goodsGridFragment.notifyCategoryChanged(category_id);
            }else {
                Toast.makeText(MainActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_category5) {
            titleText.setText("生日專區");
            category_id = 14;
            if (NetworkUtil.getConnectivityStatus(MainActivity.this)!=0) {
                goodsGridFragment.notifyCategoryChanged(category_id);
            }else {
                Toast.makeText(MainActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_category6) {
            titleText.setText("生活小物");
            category_id = 16;
            if (NetworkUtil.getConnectivityStatus(MainActivity.this)!=0) {
                goodsGridFragment.notifyCategoryChanged(category_id);
            }else {
                Toast.makeText(MainActivity.this,"無網路連線", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_service) {
            Intent searchIntent = new Intent(MainActivity.this, ServiceActivity.class);
            startActivity(searchIntent);
        } else if (id == R.id.nav_about) {
            Intent searchIntent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(searchIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Fragment getItem(int position) {
            goodsGridFragment = GoodsGridFragment.newInstance(category_id);
            return goodsGridFragment;
        }

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
            String result = UserApi.httpPostUser(user_name,real_name,gender,phone,address,fb_uid);
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


    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
//        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterPanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    public void doIncrease() {
        invalidateOptionsMenu();
    }

    public void showShoppingCarInstruction(){
        spotLightShoppingCarLayout.setVisibility(View.VISIBLE);
    }

    public void sendFragmentCategoryName(int category_id){
        String name;
        switch (category_id){
            case 10:
                name = "所有商品";
                break;
            case 11:
                name = "新品上架";
                break;
            case 12:
                name = "文具用品";
                break;
            case 13:
                name = "日韓精選";
                break;
            case 14:
                name = "生日專區";
                break;
            case 16:
                name = "生活小物";
                break;
            default:
                name = "所有商品";
        }
        mTracker.setScreenName("Fragment~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
