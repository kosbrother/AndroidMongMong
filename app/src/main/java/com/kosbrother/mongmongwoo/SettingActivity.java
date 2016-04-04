package com.kosbrother.mongmongwoo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.kosbrother.mongmongwoo.api.UserApi;
import com.kosbrother.mongmongwoo.model.User;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by kolichung on 3/17/16.
 */
public class SettingActivity extends AppCompatActivity {

    EditText nickNameEditText;
    EditText genderEditText;

    EditText shipNameEditText;
    EditText shipPhoneEditText;
    EditText shipAddressEditText;

    User user;

    String TAG = "SettingActivity";
    AccessTokenTracker accessTokenTracker;
    LoginButton loginButton;
    CallbackManager callbackManager;
    Button fb;

    String user_name = "";
    String real_name ="";
    String gender = "";
    String phone = "";
    String address = "";
    String fb_uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_back_white);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("常用設定");

        nickNameEditText = (EditText) findViewById(R.id.setting_name_edit_text);
        genderEditText = (EditText) findViewById(R.id.setting_gender_edit_text);
        shipNameEditText = (EditText) findViewById(R.id.setting_ship_name_edit_text);
        shipPhoneEditText = (EditText) findViewById(R.id.setting_ship_phone_edit_text);
        shipAddressEditText = (EditText) findViewById(R.id.setting_ship_address_edit_text);

        user = Settings.getSavedUser(this);

        nickNameEditText.setText(user.getUser_name());
        genderEditText.setText(user.getGender());

        shipNameEditText.setText(user.getReal_name());
        shipPhoneEditText.setText(user.getPhone());
        shipAddressEditText.setText(user.getAddress());

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
                            new PostUserTask().execute();

                            User theUser = new User(user_name, "", gender, "", "", fb_uid, picUrl);
                            Settings.saveUserFBData(SettingActivity.this, theUser);

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
                    Settings.clearAllUserData(SettingActivity.this);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
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

}