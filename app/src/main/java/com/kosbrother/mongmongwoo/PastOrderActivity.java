package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.github.siyamed.shapeimageview.CircularImageView;
import com.kosbrother.mongmongwoo.adpters.PastOrdersGridAdapter;
import com.kosbrother.mongmongwoo.api.WebService;
import com.kosbrother.mongmongwoo.model.PastOrder;
import com.kosbrother.mongmongwoo.model.User;
import com.kosbrother.mongmongwoo.utils.EndlessScrollListener;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.functions.Action1;

/**
 * Created by kolichung on 3/28/16.
 */
public class PastOrderActivity extends AppCompatActivity {

    User user;

    String TAG = "PastOrderActivity";
    AccessTokenTracker accessTokenTracker;
    LoginButton loginButton;
    CallbackManager callbackManager;
    Button fb;

    String user_name = "";
    String real_name = "";
    String gender = "";
    String phone = "";
    String address = "";
    String fb_uid = "";

    GridView mGridView;
    ArrayList<PastOrder> pastOrders = new ArrayList<>();
    int mPage = 1;
    PastOrdersGridAdapter pastOrdersAdapter;

    CircularImageView userImage;
    TextView userNameText;
    private Action1<ArrayList<PastOrder>> getPastOrdersNextAction =
            new Action1<ArrayList<PastOrder>>() {
                @Override
                public void call(ArrayList<PastOrder> pastOrders) {
                    if (pastOrders != null && pastOrders.size() > 0) {
                        PastOrderActivity.this.pastOrders.addAll(pastOrders);
                        mPage = mPage + 1;
                        if (pastOrdersAdapter == null) {
                            pastOrdersAdapter =
                                    new PastOrdersGridAdapter(PastOrderActivity.this, pastOrders);
                            mGridView.setAdapter(pastOrdersAdapter);
                        } else {
                            pastOrdersAdapter.notifyDataSetChanged();
                        }
                    }
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_orders);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("我的訂單");

        userImage = (CircularImageView) findViewById(R.id.user_imageview);
        userNameText = (TextView) findViewById(R.id.user_name_text);
        user = Settings.getSavedUser(PastOrderActivity.this);
        Glide.with(PastOrderActivity.this)
                .load(user.getFb_pic())
                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                .placeholder(R.drawable.icon_head)
                .crossFade()
                .into(userImage);
        userNameText.setText(user.getUser_name());

        fb = (Button) findViewById(R.id.fb);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
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

                        Bundle bFacebookData = getFacebookData(object);
                        try {
                            String picUrl = bFacebookData.getString("profile_pic");

                            user_name = bFacebookData.getString("name");
                            fb_uid = bFacebookData.getString("idFacebook");
                            gender = bFacebookData.getString("gender");

                            User theUser = new User(user_name, "", gender, "", "", fb_uid, picUrl);
                            Settings.saveUserFBData(PastOrderActivity.this, theUser);
                            WebService.postUser(theUser.getPostUserJsonString(), new Action1<Boolean>() {
                                @Override
                                public void call(Boolean response1) {
                                    if (response1) {
                                        Log.i(TAG, "成功上傳");
                                    } else {
                                        Log.i(TAG, "上傳失敗");
                                    }
                                }
                            });

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
                    Settings.clearAllUserData(PastOrderActivity.this);
                    finish();
                }
            }
        };

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        mGridView = (GridView) findViewById(R.id.fragment_gridview);
        mGridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadPastOrders();
            }
        });
        loadPastOrders();
    }

    private void loadPastOrders() {
        WebService.getPastOrdersByFbUid(user.getFb_uid(), mPage, getPastOrdersNextAction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
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
        } catch (Exception e) {
            return null;
        }
    }

}
