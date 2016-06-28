package com.kosbrother.mongmongwoo.login;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;

import rx.functions.Action1;

public class RegisterDialog extends NoTitleDialog implements View.OnClickListener {
    private Toast toast;

    public RegisterDialog(Context context) {
        super(context);
    }

    public RegisterDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RegisterDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_register);
        // Fix dialog size problem
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        ShowPasswordHelper helper = new ShowPasswordHelper();
        helper.setShowPasswordBehavior(
                findViewById(R.id.show_pw_ll),
                (CheckBox) findViewById(R.id.show_pw_cb),
                (EditText) findViewById(R.id.password_et));

        findViewById(R.id.register_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onRegisterClick();
    }

    private void onRegisterClick() {
        EditText emailEditText = (EditText) findViewById(R.id.email_et);
        EditText passwordEditText = (EditText) findViewById(R.id.password_et);
        final String emailText = emailEditText.getText().toString().trim();
        final String passwordText = passwordEditText.getText().toString().trim();

        EmailPasswordChecker checker = new EmailPasswordChecker();
        checker.check(emailText, passwordText, new EmailPasswordChecker.OnCheckResultListener() {
            @Override
            public void onCheckValid() {
                Webservice.register(emailText, passwordText, new Action1<ResponseEntity<String>>() {
                    @Override
                    public void call(ResponseEntity<String> stringResponseEntity) {
                        String data = stringResponseEntity.getData();
                        if (data != null) {
                            showAToast("註冊成功");
                            dismiss();
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

    private void showAToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
