package com.kosbrother.mongmongwoo.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.Webservice;
import com.kosbrother.mongmongwoo.databinding.DialogRegisterBinding;
import com.kosbrother.mongmongwoo.entity.ResponseEntity;
import com.kosbrother.mongmongwoo.entity.user.UserIdEntity;
import com.kosbrother.mongmongwoo.model.User;

import rx.functions.Action1;

public class RegisterDialog extends BaseNoTitleDialog implements View.OnClickListener {

    private OnRegisterSuccessListener listener = new OnRegisterSuccessListener() {
        @Override
        public void onRegisterSuccess(String email) {

        }
    };

    private Toast toast;
    private ProgressDialog progressDialog;
    private LoginUser loginUser;

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
        DialogRegisterBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.dialog_register, null, false);
        setContentView(binding.getRoot());
        loginUser = new LoginUser("", "");
        binding.setLoginUser(loginUser);

        // Fix dialog size problem
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        ShowPasswordHelper helper = new ShowPasswordHelper();
        helper.setShowPasswordBehavior(
                findViewById(R.id.show_pw_ll),
                (CheckBox) findViewById(R.id.show_pw_cb),
                (EditText) findViewById(R.id.password_et));

        findViewById(R.id.register_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onRegisterClick();
    }

    public void setOnRegisterSuccessListener(OnRegisterSuccessListener listener) {
        this.listener = listener;
    }

    private void onRegisterClick() {
        final String email = loginUser.getEmail();
        final String password = loginUser.getPassword();

        EmailPasswordChecker checker = new EmailPasswordChecker();
        checker.check(email, password, new EmailPasswordChecker.OnCheckResultListener() {
            @Override
            public void onCheckValid() {
                progressDialog = ProgressDialog.show(getContext(), "註冊中", "請稍後...", true);
                Webservice.register(email, password, new Action1<ResponseEntity<UserIdEntity>>() {
                    @Override
                    public void call(ResponseEntity<UserIdEntity> stringResponseEntity) {
                        progressDialog.dismiss();
                        UserIdEntity data = stringResponseEntity.getData();
                        if (data != null) {
                            User user = new User(email, "", "", "", email, "mmw");
                            user.setUserId(data.getUserId());
                            Settings.saveUserData(user);
                            listener.onRegisterSuccess(email);
                            listener = null;
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

    public interface OnRegisterSuccessListener {

        void onRegisterSuccess(String email);
    }
}
