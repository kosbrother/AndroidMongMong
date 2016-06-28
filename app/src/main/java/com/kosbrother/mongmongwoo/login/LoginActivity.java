package com.kosbrother.mongmongwoo.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.facebook.FbLoginActivity;

import rx.functions.Action1;

public class LoginActivity extends FbLoginActivity {

    public static final String EXTRA_STRING_EMAIL = "EXTRA_STRING_EMAIL";

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ShowPasswordHelper helper = new ShowPasswordHelper();
        helper.setShowPasswordBehavior(
                findViewById(R.id.show_pw_ll),
                (CheckBox) findViewById(R.id.show_pw_cb),
                (EditText) findViewById(R.id.password_et));

        setLoginButton((LoginButton) findViewById(R.id.fb_login_btn));
    }

    @Override
    public void onFbRequestCompleted(String fb_uid, String user_name, String picUrl) {
        onLoginSuccess(Settings.getEmail());
    }

    @Override
    public void onFbLogout() {

    }

    public void onRegisterClick(View view) {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.show();
    }

    public void onForgetPasswordClick(View view) {
        ForgetDialog dialog = new ForgetDialog(this);
        dialog.show();
    }

    public void onLoginClick(View view) {
        EditText emailEditText = (EditText) findViewById(R.id.email_et);
        EditText passwordEditText = (EditText) findViewById(R.id.password_et);
        final String emailText = emailEditText.getText().toString().trim();
        final String passwordText = passwordEditText.getText().toString().trim();

        EmailPasswordChecker checker = new EmailPasswordChecker();
        checker.check(emailText, passwordText, new EmailPasswordChecker.OnCheckResultListener() {
            @Override
            public void onCheckValid() {
                Webservice.login(emailText, passwordText, new Action1<ResponseEntity<String>>() {
                    @Override
                    public void call(ResponseEntity<String> stringResponseEntity) {
                        String data = stringResponseEntity.getData();
                        if (data != null) {
                            Settings.saveMmwUserData(emailText);
                            onLoginSuccess(emailText);
                        } else {
                            ResponseEntity.Error error = stringResponseEntity.getError();
                            showAToast(error.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCheckError(String errorMessage) {
                showAToast(errorMessage);
            }
        });
    }

    private void onLoginSuccess(String email) {
        Intent data = new Intent();
        data.putExtra(EXTRA_STRING_EMAIL, email);
        setResult(RESULT_OK, data);
        finish();
    }

    private void showAToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}