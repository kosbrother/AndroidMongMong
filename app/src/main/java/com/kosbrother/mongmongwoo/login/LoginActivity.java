package com.kosbrother.mongmongwoo.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;

public class LoginActivity extends BaseLoginActivity implements
        LoginContract.View,
        View.OnClickListener {

    private static final int REQUEST_GOOGLE_SIGN_IN = 9001;
    private static final int REQUEST_FACEBOOK_SIGN_IN = REQUEST_GOOGLE_SIGN_IN + 1;

    private LoginPresenter mPresenter;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initShowPasswordHelper();
        findViewById(R.id.fb_login_btn).setOnClickListener(this);
        findViewById(R.id.google_sign_in_btn).setOnClickListener(this);

        mPresenter = new LoginPresenter(this);
    }

    public void onRegisterClick(View view) {
        mPresenter.onMmwRegisterClick();
    }

    public void onForgetPasswordClick(View view) {
        mPresenter.onForgetPasswordClick();
    }

    public void onMmwLoginClick(View view) {
        EditText emailEditText = (EditText) findViewById(R.id.email_et);
        EditText passwordEditText = (EditText) findViewById(R.id.password_et);
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        mPresenter.onMmwLoginClick(email, password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_btn:
                mPresenter.onGoogleSignInClick();
                break;
            case R.id.fb_login_btn:
                mPresenter.onFacebookLoginClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void startGoogleSignInActivityForResult(int requestCode) {
        Intent intent = new Intent(this, GoogleSignInActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void startFacebookLoginActivityForResult(int requestCode) {
        Intent intent = new Intent(this, FacebookLogInActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.show();
    }

    @Override
    public void showForgetDialog() {
        ForgetDialog dialog = new ForgetDialog(this);
        dialog.show();
    }

    @Override
    public void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_SIGN_IN:
            case REQUEST_FACEBOOK_SIGN_IN:
                handleSignInResult(resultCode, data);
                break;
            default:
                break;
        }
    }

    private void initShowPasswordHelper() {
        ShowPasswordHelper helper = new ShowPasswordHelper();
        helper.setShowPasswordBehavior(
                findViewById(R.id.show_pw_ll),
                (CheckBox) findViewById(R.id.show_pw_cb),
                (EditText) findViewById(R.id.password_et));
    }

    private void handleSignInResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String email = data.getStringExtra(GoogleSignInActivity.EXTRA_STRING_EMAIL);
            mPresenter.onSignInResultOK(email);
        } else {
            if (data != null) {
                String errorMessage =
                        data.getStringExtra(GoogleSignInActivity.EXTRA_STRING_ERROR_MESSAGE);
                mPresenter.onSignInResultError(errorMessage);
            }
        }
    }
}