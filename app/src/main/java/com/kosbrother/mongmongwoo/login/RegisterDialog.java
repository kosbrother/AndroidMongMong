package com.kosbrother.mongmongwoo.login;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kosbrother.mongmongwoo.R;
import com.kosbrother.mongmongwoo.Settings;
import com.kosbrother.mongmongwoo.api.DataManager;
import com.kosbrother.mongmongwoo.databinding.DialogRegisterBinding;
import com.kosbrother.mongmongwoo.entity.user.MmwUserEntity;
import com.kosbrother.mongmongwoo.model.User;
import com.kosbrother.mongmongwoo.widget.CenterProgressDialog;

public class RegisterDialog extends BaseNoTitleDialog implements View.OnClickListener, DialogInterface.OnCancelListener {

    private OnRegisterSuccessListener listener = new OnRegisterSuccessListener() {
        @Override
        public void onRegisterSuccess(String email) {

        }
    };

    private Toast toast;
    private CenterProgressDialog progressDialog;
    private LoginUser loginUser;
    private DataManager.ApiCallBack callBack;

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

        ShowPasswordHelper helper = new ShowPasswordHelper();
        helper.setShowPasswordBehavior(
                findViewById(R.id.show_pw_ll),
                (CheckBox) findViewById(R.id.show_pw_cb),
                (EditText) findViewById(R.id.password_et));

        findViewById(R.id.register_tv).setOnClickListener(this);

        fixDialogSize();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        DataManager.getInstance().unSubscribe(callBack);
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
                progressDialog = CenterProgressDialog.show(getContext(), RegisterDialog.this);
                MmwUserEntity postBody = new MmwUserEntity(email, password, FirebaseInstanceId.getInstance().getToken());
                callBack = new DataManager.ApiCallBack() {
                    @Override
                    public void onError(String errorMessage) {
                        progressDialog.dismiss();
                        showAToast(errorMessage);
                    }

                    @Override
                    public void onSuccess(Object data) {
                        progressDialog.dismiss();
                        User user = new User(email, "", "", "", email, "mmw");
                        user.setUserId((Integer) data);
                        Settings.saveUserData(user);

                        listener.onRegisterSuccess(email);
                        listener = null;
                        showAToast("註冊成功");
                        dismiss();
                    }
                };
                DataManager.getInstance().postMmwRegistrations(postBody, callBack);
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
