package com.kosbrother.mongmongwoo.facebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public abstract class FbLoginActivity extends AppCompatActivity
        implements FacebookManager.FacebookListener {

    public static final String TAG = "FbLoginActivity";

    private FacebookManager fbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbManager = FacebookManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fbManager.setListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbManager.onActivityResult(requestCode, resultCode, data);
    }

    public abstract void onFbRequestCompleted(String fb_uid, String user_name, String picUrl);

    public abstract void onFbLogout();

    protected void setLoginButton(LoginButton loginButton) {
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
    }
}
