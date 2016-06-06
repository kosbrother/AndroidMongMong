package com.kosbrother.mongmongwoo;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.login.widget.LoginButton;
import com.kosbrother.mongmongwoo.facebook.FbLoginActivity;
import com.kosbrother.mongmongwoo.model.User;

/**
 * Created by kolichung on 3/17/16.
 */
public class SettingActivity extends FbLoginActivity {

    EditText nickNameEditText;
    EditText genderEditText;

    EditText shipNameEditText;
    EditText shipPhoneEditText;
    EditText shipAddressEditText;

    User user;

    String TAG = "SettingActivity";
    LoginButton loginButton;
    Button fb;

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

        user = Settings.getSavedUser();

        nickNameEditText.setText(user.getUserName());
        genderEditText.setText(user.getGender());

        shipNameEditText.setText(user.getReal_name());
        shipPhoneEditText.setText(user.getPhone());
        shipAddressEditText.setText(user.getAddress());

        loginButton = (LoginButton) findViewById(R.id.login_button);
        setLoginButton(loginButton);

        fb = (Button) findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });
    }

    @Override
    public void onFbRequestCompleted(String fb_uid, String user_name, String picUrl) {
    }

    @Override
    public void onFbLogout() {
        finish();
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

}