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
import com.jasonko.mongmongwoo.api.UserApi;
import com.jasonko.mongmongwoo.fragments.GoodsGridFragment;
import com.jasonko.mongmongwoo.model.User;

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

//    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titleText = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleText.setText("萌萌屋");

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

//        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
//        // Attach the view pager to the tab strip
//        tabsStrip.setViewPager(viewPager);
//        tabsStrip.setIndicatorColor(getResources().getColor(R.color.movie_indicator));
//        tabsStrip.setIndicatorHeight(10);
//        tabsStrip.setTextColorResource(R.color.white);
//        tabsStrip.setBackgroundColor(getResources().getColor(R.color.gray_background));

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
        if (Settings.checkIsLogIn(this)){
            User savedUser = Settings.getSavedUser(this);
            Glide.with(MainActivity.this)
                    .load(savedUser.getFb_pic())
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .placeholder(R.drawable.icon_head)
                    .crossFade()
                    .into(userImage);
            userText.setText(savedUser.getUser_name());
            loginButton.setVisibility(View.GONE);
        }else {
            loginButton.setVisibility(View.VISIBLE);
        }

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);

//        MenuItem menuItem = menu.findItem(R.id.action_shopping_car);
        MenuItem menuItem;
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        switch (id){
//            case R.id.action_search:
//                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
//                startActivity(searchIntent);
//                return true;
//            case R.id.action_shopping_car:
//                Intent shoppingCarIntent = new Intent(MainActivity.this, ShoppingCarActivity.class);
//                startActivity(shoppingCarIntent);
//                return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            if (Settings.checkIsLogIn(this)){
                Intent intent = new Intent(MainActivity.this, PastOrderActivity.class);
                startActivity(intent);
            }else {
                Toast.makeText(this, "請先使用FB登入", Toast.LENGTH_SHORT).show();
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
//        final int PAGE_COUNT = 9;
//        private String tabTitles[] = new String[]{"新品上架", "所有商品", "限時優惠","小編推薦","生日禮物","日韓精選","可愛小物","文具用品","生活用品"};

        public SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment newFragment;
//            switch (position){
//                case 0:
//                    newFragment = CategoryGoodsFragment.newInstance(1);
//                    break;
//                case 1:
//                    newFragment = GoodsGridFragment.newInstance();
//                    break;
//               default:
//                    newFragment = CategoryGoodsFragment.newInstance(position);
//
//            }
//            newFragment = CategoryGoodsFragment.newInstance(1);
            newFragment = GoodsGridFragment.newInstance();
            return newFragment;
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//            // Generate title based on item position
//            return tabTitles[position];
//        }
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

}
