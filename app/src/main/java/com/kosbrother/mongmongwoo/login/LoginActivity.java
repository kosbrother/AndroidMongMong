package com.kosbrother.mongmongwoo.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseLoginActivity implements LoginContract.View {

    private static final int REQUEST_GOOGLE_SIGN_IN = 9001;
    private static final int REQUEST_FACEBOOK_SIGN_IN = REQUEST_GOOGLE_SIGN_IN + 1;

    private LoginPresenter mPresenter;

    private Toast toast;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        LoginUser loginUser = new LoginUser("", "");
        binding.setLoginUser(loginUser);

        ShowPasswordHelper helper = new ShowPasswordHelper();
        helper.setShowPasswordBehavior(
                findViewById(R.id.show_pw_ll),
                (CheckBox) findViewById(R.id.show_pw_cb),
                (EditText) findViewById(R.id.password_et));

        mPresenter = new LoginPresenter(this, new LoginModel(loginUser));
    }

    public void onGoogleSignInClick(View view) {
        mPresenter.onGoogleSignInClick();
    }

    public void onFbLoginClick(View view) {
        mPresenter.onFacebookLoginClick();
    }

    public void onMmwLoginClick(View view) {
        mPresenter.onMmwLoginClick();
    }

    public void onRegisterClick(View view) {
        mPresenter.onMmwRegisterClick();
    }

    public void onForgetPasswordClick(View view) {
        mPresenter.onForgetPasswordClick();
    }

    @Override
    public void startGoogleSignInActivityForResult() {
        Intent intent = new Intent(this, GoogleSignInActivity.class);
        startActivityForResult(intent, REQUEST_GOOGLE_SIGN_IN);
    }

    @Override
    public void startFacebookLoginActivityForResult() {
        Intent intent = new Intent(this, FacebookLogInActivity.class);
        startActivityForResult(intent, REQUEST_FACEBOOK_SIGN_IN);
    }

    @Override
    public void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setOnRegisterSuccessListener(new RegisterDialog.OnRegisterSuccessListener() {
            @Override
            public void onRegisterSuccess(String email) {
                mPresenter.onSignInResultOK(email);
            }
        });
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
    public void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, "登入中", "請稍後...", true);
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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

    private void handleSignInResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String email = data.getStringExtra(BaseLoginActivity.EXTRA_STRING_EMAIL);
            mPresenter.onSignInResultOK(email);
        } else {
            if (data != null) {
                String errorMessage =
                        data.getStringExtra(BaseLoginActivity.EXTRA_STRING_ERROR_MESSAGE);
                mPresenter.onSignInResultError(errorMessage);
            }
        }
    }

}